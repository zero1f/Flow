package com.zero.flow.domain.model

/**
 * Represents the various states of the timer.
 */
sealed class TimerState {

    /**
     * Abstract base class for active timer states (Running or Paused).
     * This holds the common properties, allowing for safe access and smart casting.
     */
    abstract class Active(
        open val sessionType: SessionType,
        open val remainingTimeMs: Long,
        open val totalTimeMs: Long,
        open val currentTask: Task?
    ) : TimerState()

    /** The timer is not active. */
    object Idle : TimerState()

    /** The timer is currently running. */
    data class Running(
        override val sessionType: SessionType,
        override val remainingTimeMs: Long,
        override val totalTimeMs: Long,
        override val currentTask: Task? = null
    ) : Active(sessionType, remainingTimeMs, totalTimeMs, currentTask)

    /** The timer is paused. */
    data class Paused(
        override val sessionType: SessionType,
        override val remainingTimeMs: Long,
        override val totalTimeMs: Long,
        override val currentTask: Task? = null
    ) : Active(sessionType, remainingTimeMs, totalTimeMs, currentTask)

    /** A session has just been completed. */
    data class Completed(val sessionType: SessionType) : TimerState()
}
