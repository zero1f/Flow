package com.zero.flow.presentation.timer

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zero.flow.domain.model.SessionType
import com.zero.flow.domain.model.TimerState
import com.zero.flow.presentation.components.CircularTimer

@Composable
fun TimerScreen(
    onNavigateToTasks: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: TimerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.onEvent(TimerEvent.StartTimer(context))
            }
        }
    )

    DisposableEffect(Unit) {
        viewModel.bindService(context)
        onDispose {
            viewModel.unbindService(context)
        }
    }

    Scaffold(
        topBar = {
            TimerTopBar(
                onTasksClick = onNavigateToTasks,
                onStatisticsClick = onNavigateToStatistics,
                onSettingsClick = onNavigateToSettings
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Session Type Selector
            SessionTypeSelector(
                selectedType = uiState.selectedSessionType,
                onTypeSelected = { type ->
                    viewModel.onEvent(TimerEvent.SelectSessionType(type))
                },
                enabled = uiState.timerState is TimerState.Idle
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Current Task Display
            uiState.currentTask?.let { task ->
                TaskCard(
                    taskName = task.title,
                    completedPomodoros = task.completedPomodoros,
                    estimatedPomodoros = task.estimatedPomodoros,
                    onClearTask = {
                        viewModel.onEvent(TimerEvent.SelectTask(null))
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Circular Timer
            CircularTimer(
                timerState = uiState.timerState,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Timer Controls
            TimerControls(
                timerState = uiState.timerState,
                onStartClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        viewModel.onEvent(TimerEvent.StartTimer(context))
                    }
                },
                onPauseClick = { viewModel.onEvent(TimerEvent.PauseTimer(context)) },
                onResetClick = { viewModel.onEvent(TimerEvent.ResetTimer(context)) },
                onSkipClick = { viewModel.onEvent(TimerEvent.SkipTimer(context)) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Daily Progress
            DailyProgress(
                completedSessions = uiState.completedSessionsCount,
                goalSessions = uiState.settings.dailyGoal
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimerTopBar(
    onTasksClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                "Flow",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            IconButton(onClick = onTasksClick) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Tasks")
            }
            IconButton(onClick = onStatisticsClick) {
                Icon(Icons.Default.BarChart, contentDescription = "Statistics")
            }
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        }
    )
}

@Composable
private fun SessionTypeSelector(
    selectedType: SessionType,
    onTypeSelected: (SessionType) -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SessionTypeChip(
            label = "Focus",
            isSelected = selectedType == SessionType.FOCUS,
            onClick = { onTypeSelected(SessionType.FOCUS) },
            enabled = enabled
        )
        SessionTypeChip(
            label = "Short Break",
            isSelected = selectedType == SessionType.SHORT_BREAK,
            onClick = { onTypeSelected(SessionType.SHORT_BREAK) },
            enabled = enabled
        )
        SessionTypeChip(
            label = "Long Break",
            isSelected = selectedType == SessionType.LONG_BREAK,
            onClick = { onTypeSelected(SessionType.LONG_BREAK) },
            enabled = enabled
        )
    }
}

@Composable
private fun SessionTypeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.clip(CircleShape),
        color = if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun TimerControls(
    timerState: TimerState,
    onStartClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResetClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Reset/Skip button
        if (timerState !is TimerState.Idle) {
            IconButton(
                onClick = if (timerState is TimerState.Running) onSkipClick else onResetClick,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = if (timerState is TimerState.Running)
                        Icons.Default.SkipNext else Icons.Default.Refresh,
                    contentDescription = if (timerState is TimerState.Running) "Skip" else "Reset",
                    modifier = Modifier.size(32.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.size(56.dp))
        }

        // Main action button
        FilledTonalButton(
            onClick = if (timerState is TimerState.Running) onPauseClick else onStartClick,
            modifier = Modifier.size(80.dp),
            shape = CircleShape
        ) {
            Icon(
                imageVector = if (timerState is TimerState.Running)
                    Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (timerState is TimerState.Running) "Pause" else "Start",
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.size(56.dp))
    }
}

@Composable
private fun TaskCard(
    taskName: String,
    completedPomodoros: Int,
    estimatedPomodoros: Int,
    onClearTask: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = taskName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$completedPomodoros / $estimatedPomodoros pomodoros",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onClearTask) {
                Icon(Icons.Default.Close, contentDescription = "Clear task")
            }
        }
    }
}

@Composable
private fun DailyProgress(
    completedSessions: Int,
    goalSessions: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Today: $completedSessions / $goalSessions sessions",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { (completedSessions.toFloat() / goalSessions).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
        )
    }
}

private fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
