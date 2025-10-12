package com.zero.flow.domain.usecase.settings

import com.zero.flow.domain.model.Settings
import javax.inject.Inject

/**
 * Use case for validating settings
 */
class ValidateSettingsUseCase @Inject constructor() {
    operator fun invoke(settings: Settings): Result<Unit> {
        return try {
            when {
                settings.focusDuration !in 1..120 ->
                    Result.failure(IllegalArgumentException("Invalid focus duration"))

                settings.shortBreakDuration !in 1..30 ->
                    Result.failure(IllegalArgumentException("Invalid short break duration"))

                settings.longBreakDuration !in 5..60 ->
                    Result.failure(IllegalArgumentException("Invalid long break duration"))

                settings.sessionsUntilLongBreak !in 2..10 ->
                    Result.failure(IllegalArgumentException("Invalid sessions until long break"))

                settings.dailyGoal !in 1..20 ->
                    Result.failure(IllegalArgumentException("Invalid daily goal"))

                settings.ambientSoundVolume !in 0f..1f ->
                    Result.failure(IllegalArgumentException("Invalid volume"))

                else -> Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}