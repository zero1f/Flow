package com.zero.flow.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zero.flow.domain.model.TimerState

@Composable
fun TimerControls(
    timerState: TimerState,
    onStartClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResetClick: () -> Unit,
    onSkipClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Reset/Skip button (left side)
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

        // Main action button (center)
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

        // Placeholder for symmetry (right side)
        Spacer(modifier = Modifier.size(56.dp))
    }
}