package com.zero.flow.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zero.flow.domain.model.Task

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String, estimatedPomodoros: Int, color: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var estimatedPomodoros by remember { mutableStateOf("4") }
    var selectedColorIndex by remember { mutableStateOf(0) }

    val taskColors = remember {
        listOf(
            0xFF6366F1.toInt(), // Indigo
            0xFF764BA2.toInt(), // Purple
            0xFFf093fb.toInt(), // Pink
            0xFF4facfe.toInt(), // Blue
            0xFF00f2fe.toInt(), // Cyan
            0xFF43e97b.toInt(), // Green
            0xFFfa709a.toInt(), // Rose
            0xFFfee140.toInt(), // Yellow
            0xFFff6b6b.toInt(), // Red
            0xFF4ecdc4.toInt()  // Teal
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Task") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                OutlinedTextField(
                    value = estimatedPomodoros,
                    onValueChange = {
                        estimatedPomodoros = it.filter { char -> char.isDigit() }
                    },
                    label = { Text("Estimated Pomodoros") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                // Color picker
                ColorPicker(
                    colors = taskColors,
                    selectedColorIndex = selectedColorIndex,
                    onColorSelected = { selectedColorIndex = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val pomodoros = estimatedPomodoros.toIntOrNull() ?: 4
                    onConfirm(title, description, pomodoros, taskColors[selectedColorIndex])
                },
                enabled = title.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        modifier = modifier
    )
}

@Composable
fun EditTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var estimatedPomodoros by remember { mutableStateOf(task.estimatedPomodoros.toString()) }

    val taskColors = remember {
        listOf(
            0xFF6366F1.toInt(), 0xFF764BA2.toInt(), 0xFFf093fb.toInt(),
            0xFF4facfe.toInt(), 0xFF00f2fe.toInt(), 0xFF43e97b.toInt(),
            0xFFfa709a.toInt(), 0xFFfee140.toInt(), 0xFFff6b6b.toInt(),
            0xFF4ecdc4.toInt()
        )
    }

    var selectedColorIndex by remember {
        mutableStateOf(taskColors.indexOf(task.color).takeIf { it >= 0 } ?: 0)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Task") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                OutlinedTextField(
                    value = estimatedPomodoros,
                    onValueChange = {
                        estimatedPomodoros = it.filter { char -> char.isDigit() }
                    },
                    label = { Text("Estimated Pomodoros") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                // Color picker
                ColorPicker(
                    colors = taskColors,
                    selectedColorIndex = selectedColorIndex,
                    onColorSelected = { selectedColorIndex = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val pomodoros = estimatedPomodoros.toIntOrNull() ?: task.estimatedPomodoros
                    onConfirm(title, description, pomodoros, taskColors[selectedColorIndex])
                },
                enabled = title.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        modifier = modifier
    )
}

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    confirmButtonText: String = "Confirm",
    dismissButtonText: String = "Cancel"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissButtonText)
            }
        },
        modifier = modifier
    )
}

@Composable
private fun ColorPicker(
    colors: List<Int>,
    selectedColorIndex: Int,
    onColorSelected: (Int) -> Unit
) {
    Column {
        Text(
            text = "Task Color",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            colors.take(5).forEachIndexed { index, color ->
                ColorOption(
                    color = Color(color),
                    selected = selectedColorIndex == index,
                    onClick = { onColorSelected(index) }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            colors.drop(5).forEachIndexed { index, color ->
                ColorOption(
                    color = Color(color),
                    selected = selectedColorIndex == index + 5,
                    onClick = { onColorSelected(index + 5) }
                )
            }
        }
    }
}

@Composable
private fun ColorOption(
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .then(
                if (selected) {
                    Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent,
            shape = CircleShape
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
