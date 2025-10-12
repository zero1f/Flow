package com.zero.flow.domain.usecase.settings

import com.zero.flow.domain.model.AppTheme
import com.zero.flow.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject


/**
 * Use case for updating app theme
 */
class UpdateThemeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(theme: AppTheme): Result<Unit> {
        return try {
            val currentSettings = settingsRepository.getSettings().first()
            val newSettings = currentSettings.copy(theme = theme)
            settingsRepository.updateSettings(newSettings)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}