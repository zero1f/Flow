package com.zero.flow.domain.usecase.task

import com.zero.flow.domain.model.Task
import com.zero.flow.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Use case for checking if a task is complete based on pomodoros
 */
class CheckTaskCompletionUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Boolean {
        return task.completedPomodoros >= task.estimatedPomodoros
    }
}