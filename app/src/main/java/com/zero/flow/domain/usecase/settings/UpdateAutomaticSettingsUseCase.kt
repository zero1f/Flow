package com.zero.flow.domain.usecase.settings

import com.zero.flow.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for updating automatic settings
 */
class UpdateAutomaticSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(
        autoStartBreaks: Boolean,
        autoStartPomodoros: Boolean
    ): Result<Unit> {
        return try {
            val currentSettings = settingsRepository.getSettings().first()
            val newSettings = currentSettings.copy(
                autoStartBreaks = autoStartBreaks,
                autoStartPomodoros = autoStartPomodoros
            )
            settingsRepository.updateSettings(newSettings)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}