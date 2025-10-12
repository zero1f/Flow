package com.zero.flow.domain.usecase.task

import com.zero.flow.domain.model.Task
import com.zero.flow.domain.repository.TaskRepository
import java.time.LocalDateTime
import javax.inject.Inject


/**
 * Use case for marking a task as complete
 */
class CompleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Result<Unit> {
        return try {
            val updatedTask = task.copy(
                completed = true,
                completedAt = LocalDateTime.now()
            )
            taskRepository.updateTask(updatedTask)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}