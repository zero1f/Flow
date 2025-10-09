package com.zero.flow.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.zero.flow.domain.model.AmbientSoundType
import com.zero.flow.domain.model.AppTheme
import com.zero.flow.domain.model.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// ============= DATASTORE =============

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val FOCUS_DURATION = intPreferencesKey("focus_duration")
        val SHORT_BREAK_DURATION = intPreferencesKey("short_break_duration")
        val LONG_BREAK_DURATION = intPreferencesKey("long_break_duration")
        val SESSIONS_UNTIL_LONG_BREAK = intPreferencesKey("sessions_until_long_break")
        val AUTO_START_BREAKS = booleanPreferencesKey("auto_start_breaks")
        val AUTO_START_POMODOROS = booleanPreferencesKey("auto_start_pomodoros")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val AMBIENT_SOUND_TYPE = stringPreferencesKey("ambient_sound_type")
        val AMBIENT_SOUND_VOLUME = floatPreferencesKey("ambient_sound_volume")
        val DAILY_GOAL_SESSIONS = intPreferencesKey("daily_goal_sessions")
        val THEME = stringPreferencesKey("theme")
    }

    val settings: Flow<Settings> = dataStore.data.map { preferences ->
        Settings(
            focusDuration = preferences[PreferencesKeys.FOCUS_DURATION] ?: 25,
            shortBreakDuration = preferences[PreferencesKeys.SHORT_BREAK_DURATION] ?: 5,
            longBreakDuration = preferences[PreferencesKeys.LONG_BREAK_DURATION] ?: 15,
            sessionsUntilLongBreak = preferences[PreferencesKeys.SESSIONS_UNTIL_LONG_BREAK] ?: 4,
            autoStartBreaks = preferences[PreferencesKeys.AUTO_START_BREAKS] ?: false,
            autoStartPomodoros = preferences[PreferencesKeys.AUTO_START_POMODOROS] ?: false,
            soundEnabled = preferences[PreferencesKeys.SOUND_ENABLED] ?: true,
            vibrationEnabled = preferences[PreferencesKeys.VIBRATION_ENABLED] ?: true,
            ambientSound = AmbientSoundType.valueOf(
                preferences[PreferencesKeys.AMBIENT_SOUND_TYPE] ?: AmbientSoundType.NONE.name
            ),
            ambientSoundVolume = preferences[PreferencesKeys.AMBIENT_SOUND_VOLUME] ?: 0.5f,
            dailyGoal = preferences[PreferencesKeys.DAILY_GOAL_SESSIONS] ?: 8,
            theme = AppTheme.valueOf(
                preferences[PreferencesKeys.THEME] ?: AppTheme.SYSTEM.name
            )
        )
    }

    suspend fun updateSettings(settings: Settings) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.FOCUS_DURATION] = settings.focusDuration
            preferences[PreferencesKeys.SHORT_BREAK_DURATION] = settings.shortBreakDuration
            preferences[PreferencesKeys.LONG_BREAK_DURATION] = settings.longBreakDuration
            preferences[PreferencesKeys.SESSIONS_UNTIL_LONG_BREAK] = settings.sessionsUntilLongBreak
            preferences[PreferencesKeys.AUTO_START_BREAKS] = settings.autoStartBreaks
            preferences[PreferencesKeys.AUTO_START_POMODOROS] = settings.autoStartPomodoros
            preferences[PreferencesKeys.SOUND_ENABLED] = settings.soundEnabled
            preferences[PreferencesKeys.VIBRATION_ENABLED] = settings.vibrationEnabled
            preferences[PreferencesKeys.AMBIENT_SOUND_TYPE] = settings.ambientSound.name
            preferences[PreferencesKeys.AMBIENT_SOUND_VOLUME] = settings.ambientSoundVolume
            preferences[PreferencesKeys.DAILY_GOAL_SESSIONS] = settings.dailyGoal
            preferences[PreferencesKeys.THEME] = settings.theme.name
        }
    }
}