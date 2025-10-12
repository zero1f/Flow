package com.zero.flow.domain.usecase.settings

import com.zero.flow.domain.model.Settings
import com.zero.flow.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Use case for updating all settings
 */
class UpdateSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(settings: Settings): Result<Unit> {
        return try {
            // Validate settings
            if (settings.focusDuration < 1 || settings.focusDuration > 120) {
                return Result.failure(
                    IllegalArgumentException("Focus duration must be between 1 and 120 minutes")
                )
            }

            if (settings.shortBreakDuration < 1 || settings.shortBreakDuration > 30) {
                return Result.failure(
                    IllegalArgumentException("Short break must be between 1 and 30 minutes")
                )
            }

            if (settings.longBreakDuration < 5 || settings.longBreakDuration > 60) {
                return Result.failure(
                    IllegalArgumentException("Long break must be between 5 and 60 minutes")
                )
            }

            if (settings.sessionsUntilLongBreak < 2 || settings.sessionsUntilLongBreak > 10) {
                return Result.failure(
                    IllegalArgumentException("Sessions until long break must be between 2 and 10")
                )
            }

            if (settings.dailyGoal < 1 || settings.dailyGoal > 20) {
                return Result.failure(
                    IllegalArgumentException("Daily goal must be between 1 and 20 sessions")
                )
            }

            settingsRepository.updateSettings(settings)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
