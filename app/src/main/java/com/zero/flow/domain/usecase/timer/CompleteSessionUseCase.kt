package com.zero.flow.domain.usecase.timer

import com.zero.flow.domain.model.Session
import com.zero.flow.domain.model.SessionType
import com.zero.flow.domain.repository.SessionRepository
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Use case for completing a timer session and saving it
 */
class CompleteSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(
        sessionType: SessionType,
        durationMs: Long,
        startTime: LocalDateTime,
        completed: Boolean,
        taskId: Long? = null
    ): Result<Long> {
        return try {
            val session = Session(
                sessionType = sessionType,
                durationMs = durationMs,
                startTime = startTime,
                endTime = LocalDateTime.now(),
                completed = completed,
                taskId = taskId
            )

            val sessionId = sessionRepository.insertSession(session)
            Result.success(sessionId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
