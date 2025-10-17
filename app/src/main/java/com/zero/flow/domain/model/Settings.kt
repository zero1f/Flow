package com.zero.flow.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val focusDuration: Int = 25,
    val shortBreakDuration: Int = 5,
    val longBreakDuration: Int = 15,
    val sessionsUntilLongBreak: Int = 4,
    val autoStartBreaks: Boolean = false,
    val autoStartPomodoros: Boolean = false,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val ambientSound: AmbientSoundType = AmbientSoundType.NONE,
    val ambientSoundVolume: Float = 0.5f,
    val dailyGoal: Int = 8,
    val theme: AppTheme = AppTheme.SYSTEM,
    val language: String = "en",
    val autoStartNextSession: Boolean = true,
    val longBreakInterval: Int = 4
)

enum class AmbientSoundType {
    NONE,
    FIRE,
    RAIN,
    WIND,
    OCEAN,
    FOREST,
    THUNDER
}

enum class AppTheme {
    LIGHT,
    DARK,
    SYSTEM
}