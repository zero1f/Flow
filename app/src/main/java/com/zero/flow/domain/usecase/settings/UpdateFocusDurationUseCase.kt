package com.zero.flow.domain.usecase.settings

import com.zero.flow.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject


/**
 * Use case for updating focus duration
 */
class UpdateFocusDurationUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(minutes: Int): Result<Unit> {
        return try {
            if (minutes < 1 || minutes > 120) {
                return Result.failure(
                    IllegalArgumentException("Focus duration must be between 1 and 120 minutes")
                )
            }

            val currentSettings = settingsRepository.getSettings().first()
            val newSettings = currentSettings.copy(focusDuration = minutes)
            settingsRepository.updateSettings(newSettings)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}