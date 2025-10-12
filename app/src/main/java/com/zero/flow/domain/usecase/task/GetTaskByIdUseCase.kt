package com.zero.flow.domain.usecase.task

import com.zero.flow.domain.model.Task
import com.zero.flow.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Use case for getting a specific task by ID
 */
class GetTaskByIdUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: Long): Task? {
        return try {
            taskRepository.getTaskById(taskId)
        } catch (e: Exception) {
            null
        }
    }
}