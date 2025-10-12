package com.zero.flow.domain.usecase.task

import com.zero.flow.domain.model.Task
import com.zero.flow.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Use case for updating an existing task
 */
class UpdateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Result<Unit> {
        return try {
            // Validate input
            if (task.title.isBlank()) {
                return Result.failure(IllegalArgumentException("Task title cannot be empty"))
            }

            taskRepository.updateTask(task)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}