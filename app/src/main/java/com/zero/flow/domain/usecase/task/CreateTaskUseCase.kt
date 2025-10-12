package com.zero.flow.domain.usecase.task

import com.zero.flow.domain.model.Task
import com.zero.flow.domain.repository.TaskRepository
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Use case for creating a new task
 */
class CreateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(
        title: String,
        description: String = "",
        estimatedPomodoros: Int = 1,
        color: Int = 0xFF6366F1.toInt()
    ): Result<Long> {
        return try {
            // Validate input
            if (title.isBlank()) {
                return Result.failure(IllegalArgumentException("Task title cannot be empty"))
            }

            if (estimatedPomodoros < 1) {
                return Result.failure(IllegalArgumentException("Estimated pomodoros must be at least 1"))
            }

            val task = Task(
                title = title.trim(),
                description = description.trim(),
                estimatedPomodoros = estimatedPomodoros.coerceIn(1, 100),
                completedPomodoros = 0,
                completed = false,
                createdAt = LocalDateTime.now(),
                color = color
            )

            val taskId = taskRepository.insertTask(task)
            Result.success(taskId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}