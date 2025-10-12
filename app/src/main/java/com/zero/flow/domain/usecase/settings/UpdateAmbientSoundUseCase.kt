package com.zero.flow.domain.usecase.settings

import com.zero.flow.domain.model.AmbientSoundType
import com.zero.flow.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for updating ambient sound settings
 */
class UpdateAmbientSoundUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(
        soundType: AmbientSoundType,
        volume: Float
    ): Result<Unit> {
        return try {
            if (volume < 0f || volume > 1f) {
                return Result.failure(
                    IllegalArgumentException("Volume must be between 0 and 1")
                )
            }

            val currentSettings = settingsRepository.getSettings().first()
            val newSettings = currentSettings.copy(
                ambientSound = soundType,
                ambientSoundVolume = volume
            )
            settingsRepository.updateSettings(newSettings)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}