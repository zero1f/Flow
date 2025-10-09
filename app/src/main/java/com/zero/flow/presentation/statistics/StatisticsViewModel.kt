package com.zero.flow.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zero.flow.domain.model.Session
import com.zero.flow.domain.model.SessionType
import com.zero.flow.domain.repository.SessionRepository
import com.zero.flow.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    fun onEvent(event: StatisticsEvent) {
        when (event) {
            is StatisticsEvent.SelectPeriod -> {
                _uiState.update { it.copy(selectedPeriod = event.period) }
                loadStatistics()
            }
            is StatisticsEvent.RefreshData -> loadStatistics()
        }
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val period = _uiState.value.selectedPeriod
                val (startDate, endDate) = getDateRange(period)

                // Combine sessions and settings
                combine(
                    sessionRepository.getSessionsInRange(startDate, endDate),
                    settingsRepository.getSettings()
                ) { sessions, settings ->
                    calculateStatistics(sessions, settings.dailyGoal, period)
                }.collect { stats ->
                    _uiState.update {
                        it.copy(
                            totalSessions = stats.totalSessions,
                            totalFocusTimeMs = stats.totalFocusTimeMs,
                            totalBreakTimeMs = stats.totalBreakTimeMs,
                            averageSessionDurationMs = stats.averageSessionDurationMs,
                            currentStreak = stats.currentStreak,
                            longestStreak = stats.longestStreak,
                            sessionsToday = stats.sessionsToday,
                            sessionsThisWeek = stats.sessionsThisWeek,
                            sessionsThisMonth = stats.sessionsThisMonth,
                            productivityScore = stats.productivityScore,
                            dailyStatistics = stats.dailyStatistics,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load statistics"
                    )
                }
            }
        }
    }

    private fun getDateRange(period: TimePeriod): Pair<LocalDateTime, LocalDateTime> {
        val now = LocalDateTime.now()
        return when (period) {
            TimePeriod.TODAY -> {
                val startOfDay = now.toLocalDate().atStartOfDay()
                val endOfDay = startOfDay.plusDays(1).minusNanos(1)
                startOfDay to endOfDay
            }
            TimePeriod.WEEK -> {
                val startOfWeek = now.minusDays(now.dayOfWeek.value.toLong() - 1)
                    .toLocalDate().atStartOfDay()
                val endOfWeek = startOfWeek.plusDays(7).minusNanos(1)
                startOfWeek to endOfWeek
            }
            TimePeriod.MONTH -> {
                val startOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay()
                val endOfMonth = startOfMonth.plusMonths(1).minusNanos(1)
                startOfMonth to endOfMonth
            }
            TimePeriod.ALL_TIME -> {
                val startOfTime = LocalDateTime.of(2020, 1, 1, 0, 0)
                val endOfTime = now.plusYears(1)
                startOfTime to endOfTime
            }
        }
    }

    private fun calculateStatistics(
        sessions: List<Session>,
        dailyGoal: Int,
        period: TimePeriod
    ): StatisticsData {
        val completedSessions = sessions.filter { it.completed }
        val focusSessions = completedSessions.filter { it.sessionType == SessionType.FOCUS }
        val breakSessions = completedSessions.filter {
            it.sessionType == SessionType.SHORT_BREAK || it.sessionType == SessionType.LONG_BREAK
        }

        val totalSessions = focusSessions.size
        val totalFocusTimeMs = focusSessions.sumOf { it.durationMs }
        val totalBreakTimeMs = breakSessions.sumOf { it.durationMs }
        val averageSessionDurationMs = if (focusSessions.isNotEmpty()) {
            totalFocusTimeMs / focusSessions.size
        } else 0L

        // Calculate streaks
        val (currentStreak, longestStreak) = calculateStreaks(focusSessions)

        // Calculate sessions by period
        val now = LocalDateTime.now()
        val todayStart = now.toLocalDate().atStartOfDay()
        val weekStart = now.minusDays(now.dayOfWeek.value.toLong() - 1).toLocalDate().atStartOfDay()
        val monthStart = now.withDayOfMonth(1).toLocalDate().atStartOfDay()

        val sessionsToday = focusSessions.count { it.startTime >= todayStart }
        val sessionsThisWeek = focusSessions.count { it.startTime >= weekStart }
        val sessionsThisMonth = focusSessions.count { it.startTime >= monthStart }

        // Calculate productivity score (based on daily goal achievement)
        val productivityScore = if (period == TimePeriod.TODAY) {
            (sessionsToday.toFloat() / dailyGoal).coerceIn(0f, 1f)
        } else {
            val daysInPeriod = when (period) {
                TimePeriod.WEEK -> 7
                TimePeriod.MONTH -> now.month.length(now.toLocalDate().isLeapYear)
                else -> 1
            }
            val expectedSessions = dailyGoal * daysInPeriod
            if (expectedSessions > 0) {
                (totalSessions.toFloat() / expectedSessions).coerceIn(0f, 1f)
            } else 0f
        }

        // Generate daily statistics
        val dailyStats = generateDailyStatistics(focusSessions, period)

        return StatisticsData(
            totalSessions = totalSessions,
            totalFocusTimeMs = totalFocusTimeMs,
            totalBreakTimeMs = totalBreakTimeMs,
            averageSessionDurationMs = averageSessionDurationMs,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            sessionsToday = sessionsToday,
            sessionsThisWeek = sessionsThisWeek,
            sessionsThisMonth = sessionsThisMonth,
            productivityScore = productivityScore,
            dailyStatistics = dailyStats
        )
    }

    private fun calculateStreaks(sessions: List<Session>): Pair<Int, Int> {
        if (sessions.isEmpty()) return 0 to 0

        // Group sessions by date
        val sessionsByDate = sessions
            .groupBy { it.startTime.toLocalDate() }
            .mapValues { it.value.size }
            .toSortedMap()

        if (sessionsByDate.isEmpty()) return 0 to 0

        val today = LocalDateTime.now().toLocalDate()
        var currentStreak = 0
        var longestStreak = 0
        var tempStreak = 0
        var lastDate = today.plusDays(1)

        // Calculate streaks going backwards from today
        var checkDate = today
        while (checkDate >= sessionsByDate.firstKey()) {
            if (sessionsByDate.containsKey(checkDate)) {
                if (currentStreak == 0 || checkDate.plusDays(1) == lastDate) {
                    currentStreak++
                    tempStreak++
                } else {
                    break
                }
                lastDate = checkDate
            } else {
                if (currentStreak > 0) break
            }
            checkDate = checkDate.minusDays(1)
        }

        // Find longest streak
        tempStreak = 0
        var previousDate: java.time.LocalDate? = null
        sessionsByDate.keys.forEach { date ->
            if (previousDate == null || date.minusDays(1) == previousDate) {
                tempStreak++
                longestStreak = maxOf(longestStreak, tempStreak)
            } else {
                tempStreak = 1
            }
            previousDate = date
        }

        return currentStreak to longestStreak
    }

    private fun generateDailyStatistics(
        sessions: List<Session>,
        period: TimePeriod
    ): List<DailyStatistic> {
        val sessionsByDate = sessions.groupBy { it.startTime.toLocalDate() }
        val dateFormatter = DateTimeFormatter.ofPattern("MMM dd")

        val daysToShow = when (period) {
            TimePeriod.TODAY -> 1
            TimePeriod.WEEK -> 7
            TimePeriod.MONTH -> 30
            TimePeriod.ALL_TIME -> 30 // Show last 30 days for all time
        }

        val today = LocalDateTime.now().toLocalDate()

        return (0 until daysToShow).map { dayOffset ->
            val date = today.minusDays(dayOffset.toLong())
            val sessionsOnDate = sessionsByDate[date] ?: emptyList()
            val totalFocusTime = sessionsOnDate.sumOf { it.durationMs }

            DailyStatistic(
                date = date.format(dateFormatter),
                sessionsCompleted = sessionsOnDate.size,
                totalFocusTimeMs = totalFocusTime
            )
        }.reversed()
    }
}

private data class StatisticsData(
    val totalSessions: Int,
    val totalFocusTimeMs: Long,
    val totalBreakTimeMs: Long,
    val averageSessionDurationMs: Long,
    val currentStreak: Int,
    val longestStreak: Int,
    val sessionsToday: Int,
    val sessionsThisWeek: Int,
    val sessionsThisMonth: Int,
    val productivityScore: Float,
    val dailyStatistics: List<DailyStatistic>
)