package com.zero.flow.domain.model

import com.zero.flow.data.local.entity.SessionEntity
import java.time.LocalDateTime

data class Session(
    val id: Long = 0,
    val sessionType: SessionType,
    val durationMs: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val completed: Boolean,
    val taskId: Long? = null
)

fun SessionEntity.toDomain() = Session(
    id = id,
    sessionType = sessionType,
    durationMs = durationMs,
    startTime = startTime,
    endTime = endTime,
    completed = completed,
    taskId = taskId
)

fun Session.toEntity() = SessionEntity(
    id = id,
    sessionType = sessionType,
    durationMs = durationMs,
    startTime = startTime,
    endTime = endTime,
    completed = completed,
    taskId = taskId
)