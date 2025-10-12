package com.zero.flow.domain.usecase.timer

import com.zero.flow.domain.model.SessionType
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Use case for starting a timer session
 */
class StartTimerUseCase @Inject constructor() {
    operator fun invoke(
        sessionType: SessionType,
        durationMs: Long,
        taskId: Long? = null
    ): TimerData {
        return TimerData(
            sessionType = sessionType,
            startTime = LocalDateTime.now(),
            durationMs = durationMs,
            taskId = taskId
        )
    }
}