package com.zero.flow.domain.repository

import com.zero.flow.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun insertTask(task: Task): Long
    suspend fun updateTask(task: Task)
    fun getActiveTasks(): Flow<List<Task>>
    fun getAllTasks(): Flow<List<Task>>
    suspend fun getTaskById(taskId: Long): Task?
    suspend fun deleteTask(taskId: Long)
    suspend fun incrementTaskPomodoros(taskId: Long)
}