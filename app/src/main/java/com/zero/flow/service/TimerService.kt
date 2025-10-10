package com.zero.flow.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.zero.flow.domain.model.SessionType
import com.zero.flow.domain.model.TimerState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@AndroidEntryPoint
class TimerService : Service() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    private val binder = TimerBinder()
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var timerJob: Job? = null

    private val _timerState = MutableStateFlow<TimerState>(TimerState.Idle)
    val timerState = _timerState.asStateFlow()

    override fun onBind(intent: Intent?): IBinder = binder

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_TIMER -> {
                val sessionType = intent.getSerializableExtra(EXTRA_SESSION_TYPE) as? SessionType ?: SessionType.FOCUS
                val totalTime = intent.getLongExtra(EXTRA_TOTAL_TIME, 0)
                startTimer(sessionType, totalTime)
            }
            ACTION_PAUSE_TIMER -> pauseTimer()
            ACTION_STOP_TIMER -> stopTimer()
        }
        return START_NOT_STICKY
    }

    private fun startTimer(sessionType: SessionType, totalTime: Long) {
        timerJob?.cancel() // Cancel any existing timer

        val initialTime = if (_timerState.value is TimerState.Paused) {
            // If resuming, use the remaining time
            (_timerState.value as TimerState.Paused).remainingTimeMs
        } else {
            // Otherwise, start from the full duration
            totalTime
        }

        _timerState.value = TimerState.Running(sessionType, initialTime, totalTime, null)
        startForegroundService(isPaused = false)

        timerJob = serviceScope.launch {
            var remaining = initialTime
            while (remaining > 0 && isActive) {
                delay(1000)
                remaining -= 1000
                _timerState.value = TimerState.Running(sessionType, remaining, totalTime, null)
                updateNotification()
            }

            if (isActive) {
                // Timer completed normally
                _timerState.value = TimerState.Completed(sessionType)
                // Stop the service and notification after completion is signaled
                stopTimer()
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        val state = _timerState.value
        if (state is TimerState.Running) {
            _timerState.value = TimerState.Paused(state.sessionType, state.remainingTimeMs, state.totalTimeMs, state.currentTask)
            startForegroundService(isPaused = true)
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        _timerState.value = TimerState.Idle
        stopForeground(true)
        stopSelf()
    }

    private fun startForegroundService(isPaused: Boolean) {
        val state = _timerState.value
        if (state is TimerState.Active) {
            val notification = notificationHelper.createTimerNotification(
                sessionType = state.sessionType,
                remainingTime = state.remainingTimeMs,
                totalTime = state.totalTimeMs,
                isPaused = isPaused
            )
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun updateNotification() {
        val state = _timerState.value
        if (state is TimerState.Running) {
            val notification = notificationHelper.createTimerNotification(
                sessionType = state.sessionType,
                remainingTime = state.remainingTimeMs,
                totalTime = state.totalTimeMs,
                isPaused = false
            )
            notificationHelper.updateNotification(NOTIFICATION_ID, notification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        private const val NOTIFICATION_ID = 1001

        const val ACTION_START_TIMER = "com.zero.flow.ACTION_START_TIMER"
        const val ACTION_PAUSE_TIMER = "com.zero.flow.ACTION_PAUSE_TIMER"
        const val ACTION_STOP_TIMER = "com.zero.flow.ACTION_STOP_TIMER"

        const val EXTRA_SESSION_TYPE = "session_type"
        const val EXTRA_TOTAL_TIME = "total_time"

        fun newIntent(context: Context): Intent = Intent(context, TimerService::class.java)
    }
}
