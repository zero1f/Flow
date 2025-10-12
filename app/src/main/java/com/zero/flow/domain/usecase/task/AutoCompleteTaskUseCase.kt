package com.zero.flow.domain.usecase.task

import com.zero.flow.domain.repository.TaskRepository
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Use case for auto-completing a task when pomodoros are done
 */
class AutoCompleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val checkTaskCompletionUseCase: CheckTaskCompletionUseCase
) {
    suspend operator fun invoke(taskId: Long): Result<Boolean> {
        return try {
            val task = taskRepository.getTaskById(taskId)
                ?: return Result.failure(IllegalArgumentException("Task not found"))

            if (checkTaskCompletionUseCase(task) && !task.completed) {
                val updatedTask = task.copy(
                    completed = true,
                    completedAt = LocalDateTime.now()
                )
                taskRepository.updateTask(updatedTask)
                Result.success(true)
            } else {
                Result.success(false)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}