package com.zero.flow.domain.usecase.session

import com.zero.flow.domain.model.Session
import com.zero.flow.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Use case for getting sessions within a date range
 */
class GetSessionsInRangeUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<Session>> {
        return sessionRepository.getSessionsInRange(startDate, endDate)
    }
}