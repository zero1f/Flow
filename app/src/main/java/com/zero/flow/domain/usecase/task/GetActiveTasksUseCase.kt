package com.zero.flow.domain.usecase.task

import com.zero.flow.domain.model.Task
import com.zero.flow.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting only active (incomplete) tasks
 */
class GetActiveTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(): Flow<List<Task>> {
        return taskRepository.getActiveTasks()
    }
}