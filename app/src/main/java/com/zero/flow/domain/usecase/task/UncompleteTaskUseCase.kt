package com.zero.flow.domain.usecase.task

import com.zero.flow.domain.model.Task
import com.zero.flow.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Use case for marking a task as incomplete
 */
class UncompleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Result<Unit> {
        return try {
            val updatedTask = task.copy(
                completed = false,
                completedAt = null
            )
            taskRepository.updateTask(updatedTask)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}