package com.zero.flow.domain.usecase.settings

import com.zero.flow.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for updating break durations
 */
class UpdateBreakDurationUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(
        shortBreakMinutes: Int,
        longBreakMinutes: Int
    ): Result<Unit> {
        return try {
            if (shortBreakMinutes < 1 || shortBreakMinutes > 30) {
                return Result.failure(
                    IllegalArgumentException("Short break must be between 1 and 30 minutes")
                )
            }

            if (longBreakMinutes < 5 || longBreakMinutes > 60) {
                return Result.failure(
                    IllegalArgumentException("Long break must be between 5 and 60 minutes")
                )
            }

            val currentSettings = settingsRepository.getSettings().first()
            val newSettings = currentSettings.copy(
                shortBreakDuration = shortBreakMinutes,
                longBreakDuration = longBreakMinutes
            )
            settingsRepository.updateSettings(newSettings)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}