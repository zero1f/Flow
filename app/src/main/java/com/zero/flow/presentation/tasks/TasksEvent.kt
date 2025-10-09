package com.zero.flow.presentation.tasks

import com.zero.flow.domain.model.Task

sealed class TasksEvent {
    data class AddTask(
        val title: String,
        val description: String,
        val estimatedPomodoros: Int,
        val color: Int
    ) : TasksEvent()

    data class UpdateTask(val task: Task) : TasksEvent()

    data class DeleteTask(val task: Task) : TasksEvent()

    data class ToggleTaskComplete(val task: Task) : TasksEvent()

    data class IncrementPomodoros(val task: Task) : TasksEvent()

    data object ToggleShowCompleted : TasksEvent()
}