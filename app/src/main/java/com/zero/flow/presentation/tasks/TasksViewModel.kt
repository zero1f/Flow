package com.zero.flow.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zero.flow.domain.model.Task
import com.zero.flow.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    private val _showCompleted = MutableStateFlow(false)

    init {
        observeTasks()
    }

    fun onEvent(event: TasksEvent) {
        when (event) {
            is TasksEvent.AddTask -> addTask(
                title = event.title,
                description = event.description,
                estimatedPomodoros = event.estimatedPomodoros,
                color = event.color
            )
            is TasksEvent.UpdateTask -> updateTask(event.task)
            is TasksEvent.DeleteTask -> deleteTask(event.task)
            is TasksEvent.ToggleTaskComplete -> toggleTaskComplete(event.task)
            is TasksEvent.IncrementPomodoros -> incrementPomodoros(event.task)
            is TasksEvent.ToggleShowCompleted -> toggleShowCompleted()
        }
    }

    private fun observeTasks() {
        viewModelScope.launch {
            combine(
                taskRepository.getAllTasks(),
                _showCompleted
            ) { tasks, showCompleted ->
                if (showCompleted) {
                    tasks
                } else {
                    tasks.filter { !it.completed }
                }
            }
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load tasks"
                        )
                    }
                }
                .collect { filteredTasks ->
                    _uiState.update {
                        it.copy(
                            tasks = filteredTasks,
                            showCompleted = _showCompleted.value,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    private fun addTask(
        title: String,
        description: String,
        estimatedPomodoros: Int,
        color: Int
    ) {
        viewModelScope.launch {
            try {
                val task = Task(
                    title = title.trim(),
                    description = description.trim(),
                    estimatedPomodoros = estimatedPomodoros.coerceIn(1, 100),
                    completedPomodoros = 0,
                    completed = false,
                    createdAt = LocalDateTime.now(),
                    color = color
                )
                taskRepository.insertTask(task)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to add task: ${e.message}")
                }
            }
        }
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.updateTask(task)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to update task: ${e.message}")
                }
            }
        }
    }

    private fun deleteTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.deleteTask(task.id)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to delete task: ${e.message}")
                }
            }
        }
    }

    private fun toggleTaskComplete(task: Task) {
        viewModelScope.launch {
            try {
                val updatedTask = task.copy(
                    completed = !task.completed,
                    completedAt = if (!task.completed) LocalDateTime.now() else null
                )
                taskRepository.updateTask(updatedTask)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to update task: ${e.message}")
                }
            }
        }
    }

    private fun incrementPomodoros(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.incrementTaskPomodoros(task.id)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to increment pomodoros: ${e.message}")
                }
            }
        }
    }

    private fun toggleShowCompleted() {
        _showCompleted.update { !it }
    }
}