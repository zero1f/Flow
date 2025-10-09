package com.zero.flow.service

//import com.zero.flow.presentation.MainActivity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.zero.flow.domain.model.SessionType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TimerService : Service() {

    private val notificationHelper by lazy { NotificationHelper(this) }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_TIMER -> {
                val sessionType = intent.getSerializableExtra(EXTRA_SESSION_TYPE) as? SessionType
                    ?: SessionType.FOCUS
                val remainingTime = intent.getLongExtra(EXTRA_REMAINING_TIME, 0)
                startForeground(sessionType, remainingTime)
            }
            ACTION_PAUSE_TIMER -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            ACTION_COMPLETE_SESSION -> {
                val sessionType = intent.getSerializableExtra(EXTRA_SESSION_TYPE) as? SessionType
                    ?: SessionType.FOCUS
                notificationHelper.showSessionCompleteNotification(sessionType)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun startForeground(sessionType: SessionType, remainingTime: Long) {
        val notification = notificationHelper.createTimerNotification(
            sessionType = sessionType,
            remainingTime = remainingTime
        )
        startForeground(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
        const val ACTION_START_TIMER = "com.zero.flow.START_TIMER"
        const val ACTION_PAUSE_TIMER = "com.zero.flow.PAUSE_TIMER"
        const val ACTION_COMPLETE_SESSION = "com.zero.flow.COMPLETE_SESSION"
        const val EXTRA_SESSION_TYPE = "session_type"
        const val EXTRA_REMAINING_TIME = "remaining_time"

        fun startTimer(
            context: Context,
            sessionType: SessionType,
            remainingTime: Long
        ) {
            val intent = Intent(context, TimerService::class.java).apply {
                action = ACTION_START_TIMER
                putExtra(EXTRA_SESSION_TYPE, sessionType)
                putExtra(EXTRA_REMAINING_TIME, remainingTime)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun pauseTimer(context: Context) {
            val intent = Intent(context, TimerService::class.java).apply {
                action = ACTION_PAUSE_TIMER
            }
            context.startService(intent)
        }

        fun completeSession(context: Context, sessionType: SessionType) {
            val intent = Intent(context, TimerService::class.java).apply {
                action = ACTION_COMPLETE_SESSION
                putExtra(EXTRA_SESSION_TYPE, sessionType)
            }
            context.startService(intent)
        }
    }
}