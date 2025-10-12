package com.zero.flow.domain.usecase.timer

import javax.inject.Inject

/**
* Use case for skipping the current session
*/
class SkipSessionUseCase @Inject constructor() {
    operator fun invoke() {
        // Skip session - handled by ViewModel
    }
}