package com.zero.flow.presentation.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zero.flow.domain.model.Task
import com.zero.flow.presentation.components.ConfirmationDialog
import com.zero.flow.presentation.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    onNavigateBack: () -> Unit,
    viewModel: TasksViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }
    var deletingTask by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tasks") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Filter toggle
                    IconButton(
                        onClick = {
                            viewModel.onEvent(TasksEvent.ToggleShowCompleted)
                        }
                    ) {
                        Icon(
                            imageVector = if (uiState.showCompleted)
                                Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (uiState.showCompleted)
                                "Hide completed" else "Show completed"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Task") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.tasks.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Default.CheckCircle,
                        title = if (uiState.showCompleted) "No tasks yet" else "No active tasks",
                        subtitle = if (uiState.showCompleted)
                            "Add a task to get started"
                        else
                            "Add a task or show completed tasks"
                    )
                }
                else -> {
                    TasksList(
                        tasks = uiState.tasks,
                        onTaskClick = { task ->
                            viewModel.onEvent(TasksEvent.ToggleTaskComplete(task))
                        },
                        onEditClick = { task ->
                            editingTask = task
                        },
                        onDeleteClick = { task ->
                            deletingTask = task
                        },
                        onIncrementPomodoro = { task ->
                            viewModel.onEvent(TasksEvent.IncrementPomodoros(task))
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Error message
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(error)
                }
            }
        }
    }

    // Add Task Dialog
    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, description, estimatedPomodoros, color ->
                viewModel.onEvent(
                    TasksEvent.AddTask(
                        title = title,
                        description = description,
                        estimatedPomodoros = estimatedPomodoros,
                        color = color
                    )
                )
                showAddDialog = false
            }
        )
    }

    // Edit Task Dialog
    editingTask?.let { task ->
        EditTaskDialog(
            task = task,
            onDismiss = { editingTask = null },
            onConfirm = { title, description, estimatedPomodoros, color ->
                viewModel.onEvent(
                    TasksEvent.UpdateTask(
                        task = task.copy(
                            title = title,
                            description = description,
                            estimatedPomodoros = estimatedPomodoros,
                            color = color
                        )
                    )
                )
                editingTask = null
            }
        )
    }

    // Delete Confirmation Dialog
    deletingTask?.let { task ->
        ConfirmationDialog(
            title = "Delete Task",
            message = "Are you sure you want to delete \"${task.title}\"?",
            onDismiss = { deletingTask = null },
            onConfirm = {
                viewModel.onEvent(TasksEvent.DeleteTask(task))
                deletingTask = null
            },
            confirmButtonText = "Delete",
            dismissButtonText = "Cancel"
        )
    }
}

@Composable
private fun TasksList(
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit,
    onEditClick: (Task) -> Unit,
    onDeleteClick: (Task) -> Unit,
    onIncrementPomodoro: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = tasks,
            key = { it.id }
        ) { task ->
            TaskItem(
                task = task,
                onTaskClick = { onTaskClick(task) },
                onEditClick = { onEditClick(task) },
                onDeleteClick = { onDeleteClick(task) },
                onIncrementPomodoro = { onIncrementPomodoro(task) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onIncrementPomodoro: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Color indicator
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(task.color))
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Title and checkbox
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = task.completed,
                        onCheckedChange = { onTaskClick() }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = if (task.completed)
                            TextDecoration.LineThrough else null,
                        color = if (task.completed)
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Expand icon
                IconButton(
                    onClick = { expanded = !expanded }
                ) {
                    Icon(
                        imageVector = if (expanded)
                            Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Pomodoro Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "${task.completedPomodoros} / ${task.estimatedPomodoros} pomodoros",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.weight(1f))

                // Progress percentage
                val progress = if (task.estimatedPomodoros > 0) {
                    (task.completedPomodoros.toFloat() / task.estimatedPomodoros * 100).toInt()
                } else 0

                Text(
                    text = "$progress%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = {
                    if (task.estimatedPomodoros > 0) {
                        (task.completedPomodoros.toFloat() / task.estimatedPomodoros).coerceIn(0f, 1f)
                    } else 0f
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = Color(task.color),
            )

            // Expanded Content
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    // Description
                    if (task.description.isNotBlank()) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (!task.completed) {
                            OutlinedButton(
                                onClick = onIncrementPomodoro,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Pomodoro")
                            }
                        }

                        FilledTonalButton(
                            onClick = onEditClick,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit")
                        }

                        OutlinedButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete")
                        }
                    }

                    // Task metadata
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Created: ${task.createdAt.toLocalDate()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        task.completedAt?.let {
                            Text(
                                text = "Completed: ${it.toLocalDate()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, Int) -> Unit,
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
                    singleLine = true
                )

                // Color picker
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
                        taskColors.take(5).forEachIndexed { index, color ->
                            ColorOption(
                                color = Color(color),
                                selected = selectedColorIndex == index,
                                onClick = { selectedColorIndex = index }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        taskColors.drop(5).forEachIndexed { index, color ->
                            ColorOption(
                                color = Color(color),
                                selected = selectedColorIndex == index + 5,
                                onClick = { selectedColorIndex = index + 5 }
                            )
                        }
                    }
                }
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
private fun EditTaskDialog(
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
                    singleLine = true
                )

                // Color picker
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
                        taskColors.take(5).forEachIndexed { index, color ->
                            ColorOption(
                                color = Color(color),
                                selected = selectedColorIndex == index,
                                onClick = { selectedColorIndex = index }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        taskColors.drop(5).forEachIndexed { index, color ->
                            ColorOption(
                                color = Color(color),
                                selected = selectedColorIndex == index + 5,
                                onClick = { selectedColorIndex = index + 5 }
                            )
                        }
                    }
                }
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
                    Modifier.padding(2.dp)
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