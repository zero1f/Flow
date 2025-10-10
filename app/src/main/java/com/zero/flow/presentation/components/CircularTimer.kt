package com.zero.flow.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zero.flow.domain.model.TimerState

@Composable
fun CircularTimer(
    timerState: TimerState,
    modifier: Modifier = Modifier
) {
    val progress = remember(timerState) {
        when (timerState) {
            is TimerState.Active -> timerState.remainingTimeMs.toFloat() / timerState.totalTimeMs
            is TimerState.Completed -> 0f
            is TimerState.Idle -> 1f
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300),
        label = "timer_progress"
    )

    val timeText = remember(timerState) {
        when (timerState) {
            is TimerState.Active -> formatTime(timerState.remainingTimeMs)
            is TimerState.Completed -> "00:00"
            is TimerState.Idle -> "00:00"
        }
    }

    val statusText = remember(timerState) {
        when (timerState) {
            is TimerState.Running -> "Running"
            is TimerState.Paused -> "Paused"
            is TimerState.Completed -> "Completed!"
            is TimerState.Idle -> "Ready"
            // The abstract Active class doesn't need a specific branch
            // because Running and Paused cover all its concrete implementations.
            // Adding an else branch satisfies the compiler.
            else -> ""
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 24.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2

            // Background circle
            drawCircle(
                color = Color.LightGray.copy(alpha = 0.2f),
                radius = radius,
                style = Stroke(width = strokeWidth)
            )

            // Progress arc
            val sweepAngle = 360f * animatedProgress
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFF667EEA),
                        Color(0xFF764BA2),
                        Color(0xFF667EEA)
                    )
                ),
                startAngle = -90f,
                sweepAngle = -sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(
                    (size.width - radius * 2) / 2,
                    (size.height - radius * 2) / 2
                )
            )
        }

        // Timer text in center
        Box(contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
