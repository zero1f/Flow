package com.zero.flow.domain.model

// Timer State
sealed class TimerState {
    data object Idle : TimerState()
    data class Running(
        val sessionType: SessionType,
        val remainingTimeMs: Long,
        val totalTimeMs: Long,
        val currentTask: Task? = null
    ) : TimerState()

    data class Paused(
        val sessionType: SessionType,
        val remainingTimeMs: Long,
        val totalTimeMs: Long,
        val currentTask: Task? = null
    ) : TimerState()

    data class Completed(val sessionType: SessionType) : TimerState()
}
