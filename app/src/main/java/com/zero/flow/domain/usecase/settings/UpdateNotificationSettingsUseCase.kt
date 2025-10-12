package com.zero.flow.domain.usecase.settings

import com.zero.flow.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject


/**
 * Use case for updating notification settings
 */
class UpdateNotificationSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(
        soundEnabled: Boolean,
        vibrationEnabled: Boolean
    ): Result<Unit> {
        return try {
            val currentSettings = settingsRepository.getSettings().first()
            val newSettings = currentSettings.copy(
                soundEnabled = soundEnabled,
                vibrationEnabled = vibrationEnabled
            )
            settingsRepository.updateSettings(newSettings)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}