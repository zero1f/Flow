package com.zero.flow.domain.usecase.task

import com.zero.flow.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Use case for incrementing task pomodoros
 */
class IncrementTaskPomodorosUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: Long): Result<Unit> {
        return try {
            taskRepository.incrementTaskPomodoros(taskId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
