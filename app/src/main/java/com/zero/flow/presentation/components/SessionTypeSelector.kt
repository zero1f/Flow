package com.zero.flow.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.zero.flow.domain.model.SessionType

@Composable
fun SessionTypeSelector(
    selectedType: SessionType,
    onTypeSelected: (SessionType) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
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
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.clip(CircleShape),
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