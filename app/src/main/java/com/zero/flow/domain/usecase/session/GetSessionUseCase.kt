package com.zero.flow.domain.usecase.session

import com.zero.flow.domain.model.Session
import com.zero.flow.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting all sessions
 */
class GetSessionsUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(): Flow<List<Session>> {
        return sessionRepository.getAllSessions()
    }
}
