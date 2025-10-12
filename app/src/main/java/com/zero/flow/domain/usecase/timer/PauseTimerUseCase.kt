package com.zero.flow.domain.usecase.timer

import javax.inject.Inject

/**
* Use case for pausing a timer session
*/
class PauseTimerUseCase @Inject constructor() {
    operator fun invoke(remainingTimeMs: Long): Long {
        return remainingTimeMs
    }
}
