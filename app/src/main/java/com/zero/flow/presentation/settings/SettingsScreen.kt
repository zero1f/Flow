package com.zero.flow.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zero.flow.domain.model.AmbientSoundType
import com.zero.flow.domain.model.AppTheme
import com.zero.flow.presentation.components.DurationSetting
import com.zero.flow.presentation.components.SettingsSection
import com.zero.flow.presentation.components.SliderSetting
import com.zero.flow.presentation.components.SwitchSetting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Timer Duration Section
            SettingsSection(title = "Timer Duration")

            DurationSetting(
                label = "Focus Duration",
                value = uiState.settings.focusDuration,
                onValueChange = {
                    viewModel.onEvent(SettingsEvent.UpdateFocusDuration(it))
                },
                minValue = 1,
                maxValue = 120
            )

            DurationSetting(
                label = "Short Break",
                value = uiState.settings.shortBreakDuration,
                onValueChange = {
                    viewModel.onEvent(SettingsEvent.UpdateShortBreakDuration(it))
                },
                minValue = 1,
                maxValue = 30
            )

            DurationSetting(
                label = "Long Break",
                value = uiState.settings.longBreakDuration,
                onValueChange = {
                    viewModel.onEvent(SettingsEvent.UpdateLongBreakDuration(it))
                },
                minValue = 5,
                maxValue = 60
            )

            DurationSetting(
                label = "Sessions Until Long Break",
                value = uiState.settings.sessionsUntilLongBreak,
                onValueChange = {
                    viewModel.onEvent(SettingsEvent.UpdateSessionsUntilLongBreak(it))
                },
                minValue = 2,
                maxValue = 10
            )

            HorizontalDivider()

            // Automation Section
            SettingsSection(title = "Automation")

            SwitchSetting(
                label = "Auto-start Breaks",
                subtitle = "Automatically start break sessions",
                checked = uiState.settings.autoStartBreaks,
                onCheckedChange = {
                    viewModel.onEvent(SettingsEvent.UpdateAutoStartBreaks(it))
                }
            )

            SwitchSetting(
                label = "Auto-start Pomodoros",
                subtitle = "Automatically start focus sessions after breaks",
                checked = uiState.settings.autoStartPomodoros,
                onCheckedChange = {
                    viewModel.onEvent(SettingsEvent.UpdateAutoStartPomodoros(it))
                }
            )

            HorizontalDivider()

            // Notifications Section
            SettingsSection(title = "Notifications")

            SwitchSetting(
                label = "Sound",
                subtitle = "Play sound when session completes",
                checked = uiState.settings.soundEnabled,
                onCheckedChange = {
                    viewModel.onEvent(SettingsEvent.UpdateSound(it))
                }
            )

            SwitchSetting(
                label = "Vibration",
                subtitle = "Vibrate when session completes",
                checked = uiState.settings.vibrationEnabled,
                onCheckedChange = {
                    viewModel.onEvent(SettingsEvent.UpdateVibration(it))
                }
            )

            HorizontalDivider()

            // Ambient Sound Section
            SettingsSection(title = "Ambient Sound")

            AmbientSoundSelector(
                selectedType = uiState.settings.ambientSound,
                onTypeSelected = {
                    viewModel.onEvent(SettingsEvent.UpdateAmbientSoundType(it))
                }
            )

            if (uiState.settings.ambientSound != AmbientSoundType.NONE) {
                SliderSetting(
                    label = "Volume",
                    value = uiState.settings.ambientSoundVolume,
                    onValueChange = {
                        viewModel.onEvent(SettingsEvent.UpdateAmbientSoundVolume(it))
                    },
                    valueRange = 0f..1f
                )
            }

            HorizontalDivider()

            // Goals Section
            SettingsSection(title = "Daily Goal")

            DurationSetting(
                label = "Daily Sessions Goal",
                value = uiState.settings.dailyGoal,
                onValueChange = {
                    viewModel.onEvent(SettingsEvent.UpdateDailyGoal(it))
                },
                minValue = 1,
                maxValue = 20
            )

            HorizontalDivider()

            // Appearance Section
            SettingsSection(title = "Appearance")

            ThemeSelector(
                selectedTheme = uiState.settings.theme,
                onThemeSelected = {
                    viewModel.onEvent(SettingsEvent.UpdateTheme(it))
                }
            )

            // Bottom spacing
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AmbientSoundSelector(
    selectedType: AmbientSoundType,
    onTypeSelected: (AmbientSoundType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Background Sound",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        AmbientSoundType.entries.forEach { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                label = {
                    Text(
                        text = when (type) {
                            AmbientSoundType.NONE -> "None"
                            AmbientSoundType.RAIN -> "Rain"
                            AmbientSoundType.OCEAN -> "Ocean Waves"
                            AmbientSoundType.FOREST -> "Forest"
                            AmbientSoundType.COFFEE_SHOP -> "Coffee Shop"
                            AmbientSoundType.WHITE_NOISE -> "White Noise"
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ThemeSelector(
    selectedTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "App Theme",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        AppTheme.entries.forEach { theme ->
            FilterChip(
                selected = selectedTheme == theme,
                onClick = { onThemeSelected(theme) },
                label = {
                    Text(
                        text = when (theme) {
                            AppTheme.LIGHT -> "Light"
                            AppTheme.DARK -> "Dark"
                            AppTheme.SYSTEM -> "System Default"
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}