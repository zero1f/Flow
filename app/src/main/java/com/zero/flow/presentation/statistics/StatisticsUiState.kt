package com.zero.flow.presentation.statistics

data class StatisticsUiState(
    val selectedPeriod: TimePeriod = TimePeriod.WEEK,
    val totalSessions: Int = 0,
    val totalFocusTimeMs: Long = 0L,
    val totalBreakTimeMs: Long = 0L,
    val averageSessionDurationMs: Long = 0L,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val sessionsToday: Int = 0,
    val sessionsThisWeek: Int = 0,
    val sessionsThisMonth: Int = 0,
    val productivityScore: Float = 0f,
    val dailyStatistics: List<DailyStatistic> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

enum class TimePeriod {
    TODAY,
    WEEK,
    MONTH,
    ALL_TIME
}

data class DailyStatistic(
    val date: String,
    val sessionsCompleted: Int,
    val totalFocusTimeMs: Long
)