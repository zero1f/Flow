package com.zero.flow.domain.repository

import com.zero.flow.domain.model.Session
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface SessionRepository {
    suspend fun insertSession(session: Session): Long
    fun getAllSessions(): Flow<List<Session>>
    fun getSessionsInRange(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<Session>>
    suspend fun getSessionsCountForDate(date: LocalDateTime): Int
    suspend fun deleteSession(sessionId: Long)
}