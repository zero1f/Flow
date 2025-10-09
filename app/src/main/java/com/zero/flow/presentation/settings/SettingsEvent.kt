package com.zero.flow.presentation.settings

import com.zero.flow.domain.model.AmbientSoundType
import com.zero.flow.domain.model.AppTheme

sealed class SettingsEvent {
    // Timer Duration Events
    data class UpdateFocusDuration(val minutes: Int) : SettingsEvent()
    data class UpdateShortBreakDuration(val minutes: Int) : SettingsEvent()
    data class UpdateLongBreakDuration(val minutes: Int) : SettingsEvent()
    data class UpdateSessionsUntilLongBreak(val sessions: Int) : SettingsEvent()

    // Automation Events
    data class UpdateAutoStartBreaks(val enabled: Boolean) : SettingsEvent()
    data class UpdateAutoStartPomodoros(val enabled: Boolean) : SettingsEvent()

    // Notification Events
    data class UpdateSound(val enabled: Boolean) : SettingsEvent()
    data class UpdateVibration(val enabled: Boolean) : SettingsEvent()

    // Ambient Sound Events
    data class UpdateAmbientSoundType(val type: AmbientSoundType) : SettingsEvent()
    data class UpdateAmbientSoundVolume(val volume: Float) : SettingsEvent()

    // Goals Events
    data class UpdateDailyGoal(val sessions: Int) : SettingsEvent()

    // Appearance Events
    data class UpdateTheme(val theme: AppTheme) : SettingsEvent()

    // Reset Event
    data object ResetToDefaults : SettingsEvent()
}