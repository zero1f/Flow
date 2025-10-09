package com.zero.flow.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Timer Duration",
                style = MaterialTheme.typography.titleLarge
            )

            DurationSetting(
                label = "Focus Duration",
                value = settings.focusDuration,
                onValueChange = {
                    viewModel.updateSettings(settings.copy(focusDuration = it))
                }
            )

            DurationSetting(
                label = "Short Break",
                value = settings.shortBreakDuration,
                onValueChange = {
                    viewModel.updateSettings(settings.copy(shortBreakDuration = it))
                }
            )

            DurationSetting(
                label = "Long Break",
                value = settings.longBreakDuration,
                onValueChange = {
                    viewModel.updateSettings(settings.copy(longBreakDuration = it))
                }
            )

            HorizontalDivider()

            Text(
                text = "Automation",
                style = MaterialTheme.typography.titleLarge
            )

            SwitchSetting(
                label = "Auto-start Breaks",
                checked = settings.autoStartBreaks,
                onCheckedChange = {
                    viewModel.updateSettings(settings.copy(autoStartBreaks = it))
                }
            )

            SwitchSetting(
                label = "Auto-start Pomodoros",
                checked = settings.autoStartPomodoros,
                onCheckedChange = {
                    viewModel.updateSettings(settings.copy(autoStartPomodoros = it))
                }
            )

            HorizontalDivider()

            Text(
                text = "Notifications",
                style = MaterialTheme.typography.titleLarge
            )

            SwitchSetting(
                label = "Sound",
                checked = settings.soundEnabled,
                onCheckedChange = {
                    viewModel.updateSettings(settings.copy(soundEnabled = it))
                }
            )

            SwitchSetting(
                label = "Vibration",
                checked = settings.vibrationEnabled,
                onCheckedChange = {
                    viewModel.updateSettings(settings.copy(vibrationEnabled = it))
                }
            )
        }
    }
}

@Composable
private fun DurationSetting(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalIconButton(
                onClick = { if (value > 1) onValueChange(value - 1) }
            ) {
                Text("-")
            }
            Text(
                text = "$value min",
                style = MaterialTheme.typography.titleMedium
            )
            FilledTonalIconButton(
                onClick = { if (value < 120) onValueChange(value + 1) }
            ) {
                Text("+")
            }
        }
    }
}

@Composable
private fun SwitchSetting(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
