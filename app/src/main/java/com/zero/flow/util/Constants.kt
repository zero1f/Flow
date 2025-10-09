package com.zero.flow.util

import androidx.compose.ui.graphics.Color

// ============= CONSTANTS =============

object Constants {
    // Timer Defaults
    const val DEFAULT_FOCUS_DURATION_MINUTES = 25
    const val DEFAULT_SHORT_BREAK_MINUTES = 5
    const val DEFAULT_LONG_BREAK_MINUTES = 15
    const val DEFAULT_SESSIONS_UNTIL_LONG_BREAK = 4
    const val DEFAULT_DAILY_GOAL = 8

    // Timer Limits
    const val MIN_DURATION_MINUTES = 1
    const val MAX_DURATION_MINUTES = 120

    // Database
    const val DATABASE_NAME = "flow_database"
    const val DATABASE_VERSION = 1

    // Preferences
    const val PREFERENCES_NAME = "settings"

    // Task Colors
    val TASK_COLORS = listOf(
        Color(0xFF667EEA), // Indigo
        Color(0xFF764BA2), // Purple
        Color(0xFFf093fb), // Pink
        Color(0xFF4facfe), // Blue
        Color(0xFF00f2fe), // Cyan
        Color(0xFF43e97b), // Green
        Color(0xFFfa709a), // Rose
        Color(0xFFfee140), // Yellow
        Color(0xFFff6b6b), // Red
        Color(0xFF4ecdc4)  // Teal
    )

    // Notification IDs
    const val TIMER_NOTIFICATION_ID = 1001
    const val SESSION_COMPLETE_NOTIFICATION_ID = 2001

    // Intent Actions
    const val ACTION_TIMER_TICK = "com.zero.flow.TIMER_TICK"
    const val ACTION_SESSION_COMPLETE = "com.zero.flow.SESSION_COMPLETE"

    // Vibration Patterns
    val VIBRATION_PATTERN_SHORT = longArrayOf(0, 100)
    val VIBRATION_PATTERN_LONG = longArrayOf(0, 500, 250, 500)
}