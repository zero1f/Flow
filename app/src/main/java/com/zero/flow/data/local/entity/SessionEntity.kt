package com.zero.flow.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zero.flow.domain.model.Session
import com.zero.flow.domain.model.SessionType
import java.time.LocalDateTime

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "session_type")
    val sessionType: SessionType,

    @ColumnInfo(name = "duration_ms")
    val durationMs: Long,

    @ColumnInfo(name = "start_time")
    val startTime: LocalDateTime,

    @ColumnInfo(name = "end_time")
    val endTime: LocalDateTime,

    @ColumnInfo(name = "completed")
    val completed: Boolean,

    @ColumnInfo(name = "task_id")
    val taskId: Long? = null
)

private fun SessionEntity.toDomain() = Session(
    id = id,
    sessionType = sessionType,
    durationMs = durationMs,
    startTime = startTime,
    endTime = endTime,
    completed = completed,
    taskId = taskId
)

private fun Session.toEntity() = SessionEntity(
    id = id,
    sessionType = sessionType,
    durationMs = durationMs,
    startTime = startTime,
    endTime = endTime,
    completed = completed,
    taskId = taskId
)