package com.zero.flow.service

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.zero.flow.FlowApplication
import com.zero.flow.R
import com.zero.flow.domain.model.SessionType
import com.zero.flow.presentation.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class for creating and managing notifications
 */
@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val notificationManager = NotificationManagerCompat.from(context)

    /**
     * Create a notification for the running timer
     */
    fun createTimerNotification(
        sessionType: SessionType,
        remainingTime: Long,
        totalTime: Long
    ): Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val title = getSessionTitle(sessionType)
        val timeText = formatTime(remainingTime)
        val progress = if (totalTime > 0) {
            ((totalTime - remainingTime).toFloat() / totalTime * 100).toInt()
        } else 0

        return NotificationCompat.Builder(context, FlowApplication.TIMER_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText("$timeText remaining")
            .setSubText("$progress% complete")
            .setSmallIcon(R.drawable.ic_timer)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setProgress(100, progress, false)
            .addAction(createPauseAction())
            .addAction(createStopAction())
            .build()
    }

    /**
     * Update an existing notification
     */
    fun updateNotification(notificationId: Int, notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted, cannot show notification
            return
        }
        notificationManager.notify(notificationId, notification)
    }

    /**
     * Show a notification when a session is completed
     */
    fun showSessionCompleteNotification(sessionType: SessionType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted, cannot show notification
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val (title, message, icon) = getCompletionNotificationContent(sessionType)

        val notification = NotificationCompat.Builder(context, FlowApplication.SESSION_COMPLETE_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(icon)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .build()

        notificationManager.notify(COMPLETION_NOTIFICATION_ID, notification)
    }

    private fun createPauseAction(): NotificationCompat.Action {
        val pauseIntent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_PAUSE_TIMER
        }
        val pausePendingIntent = PendingIntent.getService(
            context,
            1,
            pauseIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Action.Builder(
            R.drawable.ic_timer,
            "Pause",
            pausePendingIntent
        ).build()
    }

    private fun createStopAction(): NotificationCompat.Action {
        val stopIntent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_STOP_TIMER
        }
        val stopPendingIntent = PendingIntent.getService(
            context,
            2,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Action.Builder(
            R.drawable.ic_timer,
            "Stop",
            stopPendingIntent
        ).build()
    }

    private fun getSessionTitle(sessionType: SessionType): String {
        return when (sessionType) {
            SessionType.FOCUS -> "Focus Session"
            SessionType.SHORT_BREAK -> "Short Break"
            SessionType.LONG_BREAK -> "Long Break"
        }
    }

    private fun getCompletionNotificationContent(sessionType: SessionType): Triple<String, String, Int> {
        return when (sessionType) {
            SessionType.FOCUS -> Triple(
                "Great Work! 🎉",
                "Focus session completed. Time for a break!",
                R.drawable.ic_check_circle
            )
            SessionType.SHORT_BREAK -> Triple(
                "Break Over ⏰",
                "Ready to focus again?",
                R.drawable.ic_check_circle
            )
            SessionType.LONG_BREAK -> Triple(
                "Break Complete 🌟",
                "Feeling refreshed? Let's get back to work!",
                R.drawable.ic_check_circle
            )
        }
    }

    private fun formatTime(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    companion object {
        private const val COMPLETION_NOTIFICATION_ID = 2001
    }
}
