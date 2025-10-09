package com.zero.flow.data.repository

import com.zero.flow.data.local.dao.TaskDao
import com.zero.flow.domain.model.Task
import com.zero.flow.domain.model.toDomain
import com.zero.flow.domain.model.toEntity
import com.zero.flow.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task.toEntity())
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    override fun getActiveTasks(): Flow<List<Task>> {
        return taskDao.getActiveTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTaskById(taskId: Long): Task? {
        return taskDao.getTaskById(taskId)?.toDomain()
    }

    override suspend fun deleteTask(taskId: Long) {
        taskDao.deleteTaskById(taskId)
    }

    override suspend fun incrementTaskPomodoros(taskId: Long) {
        taskDao.incrementTaskPomodoros(taskId)
    }
}

