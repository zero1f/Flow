package com.zero.flow.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime


@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "estimated_pomodoros")
    val estimatedPomodoros: Int = 1,

    @ColumnInfo(name = "completed_pomodoros")
    val completedPomodoros: Int = 0,

    @ColumnInfo(name = "completed")
    val completed: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,

    @ColumnInfo(name = "completed_at")
    val completedAt: LocalDateTime? = null,

    @ColumnInfo(name = "color")
    val color: Int = 0xFF6366F1.toInt()
)



