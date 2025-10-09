package com.zero.flow.presentation.timer


import com.zero.flow.domain.model.Settings
import com.zero.flow.domain.model.Task
import com.zero.flow.domain.model.TimerState

data class TimerUiState(
    val timerState: TimerState = TimerState.Idle,
    val settings: Settings = Settings(),
    val currentTask: Task? = null,
    val completedSessionsCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

