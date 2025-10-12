package com.zero.flow.domain.model

import java.time.LocalDateTime
import java.util.Date

data class DailyStatistics(
    val date: LocalDateTime,
    val sessionsCompleted: Int,
    val totalFocusTimeMs: Long

)
