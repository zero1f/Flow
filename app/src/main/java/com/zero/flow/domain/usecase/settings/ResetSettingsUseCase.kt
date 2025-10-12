package com.zero.flow.domain.usecase.settings

import com.zero.flow.domain.model.Settings
import com.zero.flow.domain.repository.SettingsRepository
import javax.inject.Inject


/**
 * Use case for resetting settings to defaults
 */
class ResetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            val defaultSettings = Settings()
            settingsRepository.updateSettings(defaultSettings)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}