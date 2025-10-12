package com.zero.flow.domain.usecase.task

import com.zero.flow.domain.repository.TaskRepository
import javax.inject.Inject


/**
 * Use case for deleting a task
 */
class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: Long): Result<Unit> {
        return try {
            taskRepository.deleteTask(taskId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}