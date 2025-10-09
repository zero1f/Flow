package com.zero.flow.service

//import MainActivity
import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.zero.flow.FlowApplication
import com.zero.flow.R
import com.zero.flow.domain.model.SessionType
import com.zero.flow.presentation.MainActivity

class NotificationHelper(private val context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context)

    fun createTimerNotification(
        sessionType: SessionType,
        remainingTime: Long
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

        val title = when (sessionType) {
            SessionType.FOCUS -> "Focus Session"
            SessionType.SHORT_BREAK -> "Short Break"
            SessionType.LONG_BREAK -> "Long Break"
        }

        val timeText = formatTime(remainingTime)

        return NotificationCompat.Builder(context, FlowApplication.TIMER_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(timeText)
            .setSmallIcon(R.drawable.ic_timer)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showSessionCompleteNotification(sessionType: SessionType) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val (title, message) = when (sessionType) {
            SessionType.FOCUS -> "Great Work!" to "Focus session completed. Time for a break!"
            SessionType.SHORT_BREAK -> "Break Over" to "Ready to focus again?"
            SessionType.LONG_BREAK -> "Break Complete" to "Feeling refreshed? Let's get back to work!"
        }

        val notification = NotificationCompat.Builder(context, FlowApplication.SESSION_COMPLETE_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_check_circle)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .build()

        notificationManager.notify(COMPLETION_NOTIFICATION_ID, notification)
    }

    private fun formatTime(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d remaining", minutes, seconds)
    }

    companion object {
        private const val COMPLETION_NOTIFICATION_ID = 2001
    }
}