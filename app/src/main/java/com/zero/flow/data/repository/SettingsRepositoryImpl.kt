package com.zero.flow.data.repository

import com.zero.flow.data.preferences.UserPreferencesDataStore
import com.zero.flow.domain.model.Settings
import com.zero.flow.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: UserPreferencesDataStore
) : SettingsRepository {

    override fun getSettings(): Flow<Settings> = dataStore.settings

    override suspend fun updateSettings(settings: Settings) {
        dataStore.updateSettings(settings)
    }
}