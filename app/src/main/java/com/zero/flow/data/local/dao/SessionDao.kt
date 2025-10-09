package com.zero.flow.data.local.dao


import androidx.room.*
import com.zero.flow.data.local.entity.SessionEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity): Long

    @Query("SELECT * FROM sessions ORDER BY start_time DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE completed = 1 ORDER BY start_time DESC LIMIT :limit")
    fun getCompletedSessions(limit: Int = 100): Flow<List<SessionEntity>>

    @Query("""
        SELECT * FROM sessions 
        WHERE completed = 1 
        AND start_time >= :startDate 
        AND start_time <= :endDate 
        ORDER BY start_time DESC
    """)
    fun getSessionsInRange(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<SessionEntity>>

    @Query("""
        SELECT COUNT(*) FROM sessions 
        WHERE completed = 1 
        AND session_type = 'FOCUS'
        AND DATE(start_time) = DATE(:date)
    """)
    suspend fun getSessionsCountForDate(date: LocalDateTime): Int

    @Query("SELECT * FROM sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): SessionEntity?


    @Delete
    suspend fun deleteSession(session: SessionEntity)

    @Query("DELETE FROM sessions WHERE id = :sessionId")
    suspend fun deleteSessionById(sessionId: Long)

    @Query("DELETE FROM sessions")
    suspend fun deleteAllSessions()
}