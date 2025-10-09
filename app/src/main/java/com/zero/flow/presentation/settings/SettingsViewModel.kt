package com.zero.flow.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zero.flow.data.repository.SettingsRepositoryImpl
import com.zero.flow.domain.model.Settings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepositoryImpl
) : ViewModel() {

    val settings = settingsRepository.getSettings()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Settings()
        )

    fun updateSettings(settings: Settings) {
        viewModelScope.launch {
            settingsRepository.updateSettings(settings)
        }
    }
}