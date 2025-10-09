package com.zero.flow.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zero.flow.domain.model.AmbientSoundType
import com.zero.flow.domain.model.AppTheme
import com.zero.flow.domain.model.Settings
import com.zero.flow.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.getSettings()
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
                .collect { settings ->
                    _uiState.update {
                        it.copy(
                            settings = settings,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.UpdateFocusDuration -> updateFocusDuration(event.minutes)
            is SettingsEvent.UpdateShortBreakDuration -> updateShortBreakDuration(event.minutes)
            is SettingsEvent.UpdateLongBreakDuration -> updateLongBreakDuration(event.minutes)
            is SettingsEvent.UpdateSessionsUntilLongBreak -> updateSessionsUntilLongBreak(event.sessions)
            is SettingsEvent.UpdateAutoStartBreaks -> updateAutoStartBreaks(event.enabled)
            is SettingsEvent.UpdateAutoStartPomodoros -> updateAutoStartPomodoros(event.enabled)
            is SettingsEvent.UpdateSound -> updateSound(event.enabled)
            is SettingsEvent.UpdateVibration -> updateVibration(event.enabled)
            is SettingsEvent.UpdateAmbientSoundType -> updateAmbientSoundType(event.type)
            is SettingsEvent.UpdateAmbientSoundVolume -> updateAmbientSoundVolume(event.volume)
            is SettingsEvent.UpdateDailyGoal -> updateDailyGoal(event.sessions)
            is SettingsEvent.UpdateTheme -> updateTheme(event.theme)
            is SettingsEvent.ResetToDefaults -> resetToDefaults()
        }
    }

    private fun updateFocusDuration(minutes: Int) {
        viewModelScope.launch {
            val updatedSettings = _uiState.value.settings.copy(
                focusDuration = minutes
            )
            settingsRepository.updateSettings(updatedSettings)
        }
    }

    private fun updateShortBreakDuration(minutes: Int) {
        viewModelScope.launch {
            val updatedSettings = _uiState.value.settings.copy(
                shortBreakDuration = minutes
            )
            settingsRepository.updateSettings(updatedSettings)
        }
    }

    private fun updateLongBreakDuration(minutes: Int) {
        viewModelScope.launch {
            val updatedSettings = _uiState.value.settings.copy(
                longBreakDuration= minutes
            )
            settingsRepository.updateSettings(updatedSettings)
        }
    }

    private fun updateSessionsUntilLongBreak(sessions: Int) {
        viewModelScope.launch {
            val updatedSettings = _uiState.value.settings.copy(
                sessionsUntilLongBreak = sessions
            )
            settingsRepository.updateSettings(updatedSettings)
        }
    }

    private fun updateAutoStartBreaks(enabled: Boolean) {
        viewModelScope.launch {
            val updatedSettings = _uiState.value.settings.copy(
                autoStartBreaks = enabled
            )
            settingsRepository.updateSettings(updatedSettings)
        }
    }

    private fun updateAutoStartPomodoros(enabled: Boolean) {
        viewModelScope.launch {
            val updatedSettings = _uiState.value.settings.copy(
                autoStartPomodoros = enabled
            )
            settingsRepository.updateSettings(updatedSettings)
        }
    }

    private fun updateSound(enabled: Boolean) {
        viewModelScope.launch {
            val updatedSettings = _uiState.value.settings.copy(
                soundEnabled = enabled
            )
            settingsRepository.updateSettings(updatedSettings)
        }
    }

    private fun updateVibration(enabled: Boolean) {
        viewModelScope.launch {
            val updatedSettings = _uiState.value.settings.copy(
                vibrationEnabled = enabled
            )
            settingsRepository.updateSettings(updatedSettings)
        }
    }

    private fun updateAmbientSoundType(type: AmbientSoundType) {
        viewModelScope.launch {
            val updatedSettings = _uiState.value.settings.copy(
                ambientSound = type
            )
            settingsRepository.updateSettings(updatedSettings)
        }
    }

    private fun updateAmbientSoundVolume(volume: Float) {
        viewModelScope.launch {
            val updatedSettings = _uiState.value.settings.copy(
                ambientSoundVolume = volume
            )
            settingsRepository.updateSettings(updatedSettings)
        }
    }

    private fun updateDailyGoal(sessions: Int) {
        viewModelScope.launch {
            val updatedSettings = _uiState.value.settings.copy(
                dailyGoal = sessions
            )
            settingsRepository.updateSettings(updatedSettings)
        }
    }

    private fun updateTheme(theme: AppTheme) {
        viewModelScope.launch {
            val updatedSettings = _uiState.value.settings.copy(
                theme = theme
            )
            settingsRepository.updateSettings(updatedSettings)
        }
    }

    private fun resetToDefaults() {
        viewModelScope.launch {
            val defaultSettings = Settings()
            settingsRepository.updateSettings(defaultSettings)
        }
    }
}