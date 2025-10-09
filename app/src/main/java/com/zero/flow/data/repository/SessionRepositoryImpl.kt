package com.zero.flow.data.repository

import com.zero.flow.data.local.dao.SessionDao
import com.zero.flow.domain.model.Session
import com.zero.flow.domain.model.toDomain
import com.zero.flow.domain.model.toEntity
import com.zero.flow.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao
) : SessionRepository {


    override suspend fun insertSession(session: Session): Long {
        return sessionDao.insertSession(session.toEntity())
    }

    override fun getAllSessions(): Flow<List<Session>> {
        return sessionDao.getAllSessions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSessionsInRange(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<Session>> {
        return sessionDao.getSessionsInRange(startDate, endDate).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getSessionsCountForDate(date: LocalDateTime): Int {
        return sessionDao.getSessionsCountForDate(date)
    }

    override suspend fun deleteSession(sessionId: Long) {
        sessionDao.deleteSessionById(sessionId)
    }
}