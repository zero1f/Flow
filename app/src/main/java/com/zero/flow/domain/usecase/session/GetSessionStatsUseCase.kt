package com.zero.flow.domain.usecase.session

import com.zero.flow.domain.model.Session
import com.zero.flow.domain.model.SessionStatistics
import com.zero.flow.domain.model.SessionType
import com.zero.flow.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject


/**
 * Use case for getting session statistics
 */
class GetSessionStatsUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<SessionStatistics> {
        return sessionRepository.getSessionsInRange(startDate, endDate)
            .map { sessions ->
                calculateStatistics(sessions)
            }
    }

    private fun calculateStatistics(sessions: List<Session>): SessionStatistics {
        val completedSessions = sessions.filter { it.completed }
        val focusSessions = completedSessions.filter { it.sessionType == SessionType.FOCUS }
        val breakSessions = completedSessions.filter {
            it.sessionType == SessionType.SHORT_BREAK || it.sessionType == SessionType.LONG_BREAK
        }

        val totalSessions = focusSessions.size
        val totalFocusTime = focusSessions.sumOf { it.durationMs }
        val totalBreakTime = breakSessions.sumOf { it.durationMs }
        val averageSessionDuration = if (focusSessions.isNotEmpty()) {
            totalFocusTime / focusSessions.size
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

        // Simple productivity score (can be enhanced)
        val productivityScore = if (totalSessions > 0) {
            (sessionsToday.toFloat() / 8).coerceIn(0f, 1f)
        } else 0f

        return SessionStatistics(
            totalSessions = totalSessions,
            totalFocusTime = totalFocusTime,
            totalBreakTime = totalBreakTime,
            averageSessionDuration = averageSessionDuration,
            longestStreak = longestStreak,
            currentStreak = currentStreak,
            sessionsToday = sessionsToday,
            sessionsThisWeek = sessionsThisWeek,
            sessionsThisMonth = sessionsThisMonth,
            productivityScore = productivityScore
        )
    }

    private fun calculateStreaks(sessions: List<Session>): Pair<Int, Int> {
        if (sessions.isEmpty()) return 0 to 0

        val sessionsByDate = sessions
            .groupBy { it.startTime.toLocalDate() }
            .toSortedMap()

        val today = LocalDateTime.now().toLocalDate()
        var currentStreak = 0
        var longestStreak = 0
        var tempStreak = 0
        var checkDate = today

        // Calculate current streak
        while (checkDate >= sessionsByDate.firstKey()) {
            if (sessionsByDate.containsKey(checkDate)) {
                currentStreak++
            } else {
                if (currentStreak > 0) break
            }
            checkDate = checkDate.minusDays(1)
        }

        // Calculate longest streak
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
}