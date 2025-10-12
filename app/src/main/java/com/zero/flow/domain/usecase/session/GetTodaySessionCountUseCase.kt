package com.zero.flow.domain.usecase.session

import com.zero.flow.domain.repository.SessionRepository
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Use case for getting today's session count
 */
class GetTodaySessionCountUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(): Int {
        val today = LocalDateTime.now()
        return sessionRepository.getSessionsCountForDate(today)
    }
}