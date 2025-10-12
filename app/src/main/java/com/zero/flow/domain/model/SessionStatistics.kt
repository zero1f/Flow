package com.zero.flow.domain.model

data class SessionStatistics(
    val totalSessions: Int,
    val totalFocusTime: Long,
    val totalBreakTime: Long,
    val averageSessionDuration: Long,
    val longestStreak: Int,
    val currentStreak: Int,
    val sessionsToday: Int,
    val sessionsThisWeek: Int,
    val sessionsThisMonth: Int,
    val productivityScore: Float

)