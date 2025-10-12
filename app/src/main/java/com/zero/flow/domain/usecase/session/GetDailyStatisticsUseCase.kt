package com.zero.flow.domain.usecase.session

import com.zero.flow.domain.model.DailyStatistics
import com.zero.flow.domain.model.Session
import com.zero.flow.domain.model.SessionType
import com.zero.flow.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject


/**
 * Use case for getting daily statistics for a date range
 */
class GetDailyStatisticsUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<DailyStatistics>> {
        return sessionRepository.getSessionsInRange(startDate, endDate)
            .map { sessions ->
                generateDailyStats(sessions, startDate, endDate)
            }
    }

    private fun generateDailyStats(
        sessions: List<Session>,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<DailyStatistics> {
        val focusSessions = sessions.filter {
            it.completed && it.sessionType == SessionType.FOCUS
        }

        val sessionsByDate = focusSessions.groupBy { it.startTime.toLocalDate() }

        val result = mutableListOf<DailyStatistics>()
        var currentDate = startDate.toLocalDate()

        while (currentDate <= endDate.toLocalDate()) {
            val sessionsOnDate = sessionsByDate[currentDate] ?: emptyList()
            val totalFocusTime = sessionsOnDate.sumOf { it.durationMs }

            result.add(
                DailyStatistics(
                    date = currentDate.atStartOfDay(),
                    sessionsCompleted = sessionsOnDate.size,
                    totalFocusTimeMs = totalFocusTime
                )
            )

            currentDate = currentDate.plusDays(1)
        }

        return result
    }
}