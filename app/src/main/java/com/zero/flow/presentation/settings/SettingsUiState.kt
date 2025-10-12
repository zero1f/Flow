package com.zero.flow.presentation.settings

import com.zero.flow.domain.model.Settings

data class SettingsUiState(
    val settings: Settings = Settings(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val showResetConfirmation: Boolean = false
)