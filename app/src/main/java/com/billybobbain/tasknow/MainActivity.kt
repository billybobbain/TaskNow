// ===== MainActivity.kt =====
package com.billybobbain.tasknow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip

class MainActivity : ComponentActivity() {
    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settings by taskViewModel.settings.collectAsState(initial = null)
            val themeName = settings?.themeName ?: "Purple"

            TaskNowTheme(themeName = themeName) {
                TaskPlannerApp(taskViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskPlannerApp(viewModel: TaskViewModel) {
    var currentScreen by remember { mutableStateOf("list") }
    val tasks by viewModel.allTasks.collectAsState(initial = emptyList())
    var editingTask by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Anti-Procrastination Planner") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    IconButton(onClick = { currentScreen = "settings" }) {
                        Text("⚙", fontSize = 20.sp)
                    }
                }
            )
        },
        floatingActionButton = {
            if (currentScreen == "list") {
                FloatingActionButton(
                    onClick = {
                        editingTask = null
                        currentScreen = "form"
                    }
                ) {
                    Text("+", fontSize = 24.sp)
                }
            }
        }
    ) { padding ->
        when (currentScreen) {
            "list" -> TaskListScreen(
                tasks = tasks,
                modifier = Modifier.padding(padding),
                onTaskClick = { task ->
                    editingTask = task
                    currentScreen = "detail"
                },
                onDeleteTask = { task ->
                    viewModel.delete(task)
                }
            )
            "form" -> TaskFormScreen(
                modifier = Modifier.padding(padding),
                existingTask = editingTask,
                onSave = { task ->
                    if (editingTask != null) {
                        viewModel.update(task)
                    } else {
                        viewModel.insert(task)
                    }
                    currentScreen = "list"
                },
                onCancel = { currentScreen = "list" }
            )
            "detail" -> TaskDetailScreen(
                task = editingTask!!,
                modifier = Modifier.padding(padding),
                onEdit = { currentScreen = "form" },
                onBack = { currentScreen = "list" }
            )
            "settings" -> SettingsScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(padding),
                onBack = { currentScreen = "list" }
            )
        }
    }
}

@Composable
fun SettingsScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsState(initial = null)
    val currentTheme = settings?.themeName ?: "Purple"

    val themes = listOf("Purple", "Blue", "Green", "Orange", "Pink", "Teal")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("← Back")
        }

        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Theme",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )

        themes.forEach { theme ->
            ThemeOption(
                themeName = theme,
                isSelected = theme == currentTheme,
                onClick = { viewModel.saveTheme(theme) }
            )
        }
    }
}

@Composable
fun ThemeOption(
    themeName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = themeName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            if (isSelected) {
                Text("✓", fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun TaskListScreen(
    tasks: List<Task>,
    modifier: Modifier = Modifier,
    onTaskClick: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    "No tasks yet.\nTap + to add a task!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tasks) { task ->
                    TaskCard(
                        task = task,
                        onClick = { onTaskClick(task) },
                        onDelete = { onDeleteTask(task) }
                    )
                }
            }
        }

        Next24HoursSummary(tasks = tasks)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(task: Task, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = task.taskName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete) {
                    Text("×", fontSize = 24.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Next step: ${task.subtask}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Time: ${task.timeEstimate} min",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TaskFormScreen(
    modifier: Modifier = Modifier,
    existingTask: Task?,
    onSave: (Task) -> Unit,
    onCancel: () -> Unit
) {
    var taskName by remember { mutableStateOf(existingTask?.taskName ?: "") }
    var description by remember { mutableStateOf(existingTask?.description ?: "") }
    var avoidanceReason by remember { mutableStateOf(existingTask?.avoidanceReason ?: "") }
    var benefits by remember { mutableStateOf(existingTask?.benefits ?: "") }
    var subtask by remember { mutableStateOf(existingTask?.subtask ?: "") }
    var timeEstimate by remember { mutableStateOf(existingTask?.timeEstimate ?: "") }
    var reward by remember { mutableStateOf(existingTask?.reward ?: "") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (existingTask != null) "Edit Task" else "Add New Task",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        QuestionField(
            question = "What task are you currently procrastinating on?",
            value = taskName,
            onValueChange = { taskName = it }
        )

        QuestionField(
            question = "Provide a brief description of the task.",
            value = description,
            onValueChange = { description = it },
            multiline = true
        )

        QuestionField(
            question = "Why are you avoiding doing this task?",
            value = avoidanceReason,
            onValueChange = { avoidanceReason = it },
            multiline = true
        )

        QuestionField(
            question = "What are the benefits of completing this task?",
            value = benefits,
            onValueChange = { benefits = it },
            multiline = true
        )

        QuestionField(
            question = "Name an easy subtask you can complete for this task",
            value = subtask,
            onValueChange = { subtask = it }
        )

        QuestionField(
            question = "How long will it take you to complete this subtask (in minutes)?",
            value = timeEstimate,
            onValueChange = { timeEstimate = it }
        )

        QuestionField(
            question = "Name a small reward for completing the subtask.",
            value = reward,
            onValueChange = { reward = it }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    if (taskName.isNotBlank()) {
                        onSave(
                            Task(
                                id = existingTask?.id ?: java.util.UUID.randomUUID().toString(),
                                taskName = taskName,
                                description = description,
                                avoidanceReason = avoidanceReason,
                                benefits = benefits,
                                subtask = subtask,
                                timeEstimate = timeEstimate,
                                reward = reward
                            )
                        )
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = taskName.isNotBlank()
            ) {
                Text("Save")
            }
        }
    }
}

@Composable
fun QuestionField(
    question: String,
    value: String,
    onValueChange: (String) -> Unit,
    multiline: Boolean = false
) {
    Column {
        Text(
            text = question,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            minLines = if (multiline) 3 else 1,
            maxLines = if (multiline) 5 else 1
        )
    }
}

@Composable
fun TaskDetailScreen(
    task: Task,
    modifier: Modifier = Modifier,
    onEdit: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onBack) {
                Text("← Back")
            }
            TextButton(onClick = onEdit) {
                Text("Edit")
            }
        }

        Text(
            text = task.taskName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        DetailSection("Description", task.description)
        DetailSection("Why I'm avoiding it", task.avoidanceReason)
        DetailSection("Benefits", task.benefits)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Action Plan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                DetailSection("First subtask", task.subtask)
                DetailSection("Time needed", "${task.timeEstimate} minutes")
                DetailSection("Reward", task.reward)
            }
        }
    }
}

@Composable
fun DetailSection(label: String, content: String) {
    if (content.isNotBlank()) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun Next24HoursSummary(tasks: List<Task>) {
    val totalMinutes = tasks.sumOf {
        it.timeEstimate.toIntOrNull() ?: 0
    }
    val totalHours = totalMinutes / 60.0
    val percentageOfDay = (totalMinutes / (24.0 * 60.0) * 100).coerceAtMost(100.0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Next 24 Hours Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total Time",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = if (totalHours >= 1) {
                            String.format("%.1fh", totalHours)
                        } else {
                            "${totalMinutes}m"
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                    Text(
                        text = "Day Used",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = String.format("%.1f%%", percentageOfDay),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column {
                LinearProgressIndicator(
                    progress = { (percentageOfDay / 100.0).toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    color = when {
                        percentageOfDay < 25 -> MaterialTheme.colorScheme.primary
                        percentageOfDay < 50 -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.error
                    },
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${tasks.size} task${if (tasks.size != 1) "s" else ""} planned",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}
