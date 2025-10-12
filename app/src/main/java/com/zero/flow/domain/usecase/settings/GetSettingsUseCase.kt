package com.zero.flow.domain.usecase.settings

import com.zero.flow.domain.model.Settings
import com.zero.flow.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting app settings
 */
class GetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<Settings> {
        return settingsRepository.getSettings()
    }
}