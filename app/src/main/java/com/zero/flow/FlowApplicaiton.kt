package com.zero.flow

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.content.getSystemService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FlowApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService<NotificationManager>()

            // Timer notification channel
            val timerChannel = NotificationChannel(
                TIMER_CHANNEL_ID,
                "Timer",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows the current timer status"
                setShowBadge(false)
            }

            // Session complete notification channel
            val sessionChannel = NotificationChannel(
                SESSION_COMPLETE_CHANNEL_ID,
                "Session Complete",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies when a focus or break session is complete"
                enableVibration(true)
            }

            notificationManager?.createNotificationChannel(timerChannel)
            notificationManager?.createNotificationChannel(sessionChannel)
        }
    }

    companion object {
        const val TIMER_CHANNEL_ID = "timer_channel"
        const val SESSION_COMPLETE_CHANNEL_ID = "session_complete_channel"
    }
}