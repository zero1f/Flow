package com.zero.flow.domain.usecase.session

import com.zero.flow.domain.repository.SessionRepository
import javax.inject.Inject

/**
 * Use case for deleting a session
 */
class DeleteSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(sessionId: Long): Result<Unit> {
        return try {
            sessionRepository.deleteSession(sessionId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}