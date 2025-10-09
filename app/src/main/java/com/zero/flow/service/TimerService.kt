package com.zero.flow.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.zero.flow.FlowApplication
import com.zero.flow.R
import com.zero.flow.domain.model.SessionType
import com.zero.flow.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Foreground service that keeps the timer running in the background.
 * Displays a notification with the current timer state.
 */
@AndroidEntryPoint
class TimerService : Service() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_TIMER -> {
                val sessionType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getSerializableExtra(EXTRA_SESSION_TYPE, SessionType::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getSerializableExtra(EXTRA_SESSION_TYPE) as? SessionType
                } ?: SessionType.FOCUS

                val remainingTime = intent.getLongExtra(EXTRA_REMAINING_TIME, 0)
                val totalTime = intent.getLongExtra(EXTRA_TOTAL_TIME, remainingTime)

                startForegroundService(sessionType, remainingTime, totalTime)
            }
            ACTION_UPDATE_TIMER -> {
                val sessionType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getSerializableExtra(EXTRA_SESSION_TYPE, SessionType::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getSerializableExtra(EXTRA_SESSION_TYPE) as? SessionType
                } ?: SessionType.FOCUS

                val remainingTime = intent.getLongExtra(EXTRA_REMAINING_TIME, 0)
                val totalTime = intent.getLongExtra(EXTRA_TOTAL_TIME, remainingTime)

                updateNotification(sessionType, remainingTime, totalTime)
            }
            ACTION_PAUSE_TIMER -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            ACTION_STOP_TIMER -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            ACTION_COMPLETE_SESSION -> {
                val sessionType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getSerializableExtra(EXTRA_SESSION_TYPE, SessionType::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getSerializableExtra(EXTRA_SESSION_TYPE) as? SessionType
                } ?: SessionType.FOCUS

                notificationHelper.showSessionCompleteNotification(sessionType)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun startForegroundService(
        sessionType: SessionType,
        remainingTime: Long,
        totalTime: Long
    ) {
        val notification = notificationHelper.createTimerNotification(
            sessionType = sessionType,
            remainingTime = remainingTime,
            totalTime = totalTime
        )
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun updateNotification(
        sessionType: SessionType,
        remainingTime: Long,
        totalTime: Long
    ) {
        val notification = notificationHelper.createTimerNotification(
            sessionType = sessionType,
            remainingTime = remainingTime,
            totalTime = totalTime
        )
        notificationHelper.updateNotification(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    companion object {
        private const val NOTIFICATION_ID = 1001

        // Actions
        const val ACTION_START_TIMER = "com.zero.flow.ACTION_START_TIMER"
        const val ACTION_UPDATE_TIMER = "com.zero.flow.ACTION_UPDATE_TIMER"
        const val ACTION_PAUSE_TIMER = "com.zero.flow.ACTION_PAUSE_TIMER"
        const val ACTION_STOP_TIMER = "com.zero.flow.ACTION_STOP_TIMER"
        const val ACTION_COMPLETE_SESSION = "com.zero.flow.ACTION_COMPLETE_SESSION"

        // Extras
        const val EXTRA_SESSION_TYPE = "session_type"
        const val EXTRA_REMAINING_TIME = "remaining_time"
        const val EXTRA_TOTAL_TIME = "total_time"

        /**
         * Start the timer service and display foreground notification
         */
        fun startTimer(
            context: Context,
            sessionType: SessionType,
            remainingTime: Long,
            totalTime: Long
        ) {
            val intent = Intent(context, TimerService::class.java).apply {
                action = ACTION_START_TIMER
                putExtra(EXTRA_SESSION_TYPE, sessionType)
                putExtra(EXTRA_REMAINING_TIME, remainingTime)
                putExtra(EXTRA_TOTAL_TIME, totalTime)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        /**
         * Update the timer notification with new time
         */
        fun updateTimer(
            context: Context,
            sessionType: SessionType,
            remainingTime: Long,
            totalTime: Long
        ) {
            val intent = Intent(context, TimerService::class.java).apply {
                action = ACTION_UPDATE_TIMER
                putExtra(EXTRA_SESSION_TYPE, sessionType)
                putExtra(EXTRA_REMAINING_TIME, remainingTime)
                putExtra(EXTRA_TOTAL_TIME, totalTime)
            }
            context.startService(intent)
        }

        /**
         * Pause the timer and remove notification
         */
        fun pauseTimer(context: Context) {
            val intent = Intent(context, TimerService::class.java).apply {
                action = ACTION_PAUSE_TIMER
            }
            context.startService(intent)
        }

        /**
         * Stop the timer service
         */
        fun stopTimer(context: Context) {
            val intent = Intent(context, TimerService::class.java).apply {
                action = ACTION_STOP_TIMER
            }
            context.startService(intent)
        }

        /**
         * Complete the session and show completion notification
         */
        fun completeSession(context: Context, sessionType: SessionType) {
            val intent = Intent(context, TimerService::class.java).apply {
                action = ACTION_COMPLETE_SESSION
                putExtra(EXTRA_SESSION_TYPE, sessionType)
            }
            context.startService(intent)
        }
    }
}