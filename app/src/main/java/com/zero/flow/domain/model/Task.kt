package com.zero.flow.domain.model

import com.zero.flow.data.local.entity.TaskEntity
import java.time.LocalDateTime

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val estimatedPomodoros: Int = 1,
    val completedPomodoros: Int = 0,
    val completed: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val completedAt: LocalDateTime? = null,
    val color: Int = 0xFF6366F1.toInt()
)

public fun TaskEntity.toDomain() = Task(
    id = id,
    title = title,
    description = description,
    estimatedPomodoros = estimatedPomodoros,
    completedPomodoros = completedPomodoros,
    completed = completed,
    createdAt = createdAt,
    completedAt = completedAt,
    color = color
)

public fun Task.toEntity() = TaskEntity(
    id = id,
    title = title,
    description = description,
    estimatedPomodoros = estimatedPomodoros,
    completedPomodoros = completedPomodoros,
    completed = completed,
    createdAt = createdAt,
    completedAt = completedAt,
    color = color
)