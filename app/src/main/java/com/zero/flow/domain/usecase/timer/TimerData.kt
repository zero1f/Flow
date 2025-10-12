package com.zero.flow.domain.usecase.timer

import com.zero.flow.domain.model.SessionType
import java.time.LocalDateTime


data class TimerData(
    val sessionType: SessionType,
    val startTime: LocalDateTime,
    val durationMs: Long,
    val taskId: Long?
)


