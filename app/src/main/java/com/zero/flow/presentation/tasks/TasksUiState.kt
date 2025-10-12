package com.zero.flow.presentation.tasks

import com.zero.flow.domain.model.Task

data class TasksUiState(
    val tasks: List<Task> = emptyList(),
    val showCompleted: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)