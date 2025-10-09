package com.zero.flow.presentation.tasks


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zero.flow.domain.model.Task
import com.zero.flow.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    val tasks = taskRepository.getAllTasks()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            taskRepository.deleteTask(taskId)
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(
                task.copy(
                    completed = !task.completed,
                    completedAt = if (!task.completed) java.time.LocalDateTime.now() else null
                )
            )
        }
    }
}

