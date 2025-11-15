// ===== MainActivity.kt =====
package com.billybobbain.tasknow

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip

class MainActivity : ComponentActivity() {
    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize OSMDroid configuration
        org.osmdroid.config.Configuration.getInstance().load(
            this,
            getSharedPreferences("osmdroid", MODE_PRIVATE)
        )
        org.osmdroid.config.Configuration.getInstance().userAgentValue = packageName

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
                viewModel = viewModel,
                modifier = Modifier.padding(padding),
                onTaskClick = { task ->
                    editingTask = task
                    currentScreen = "detail"
                },
                onDeleteTask = { task ->
                    viewModel.delete(task)
                },
                onManageLocations = { currentScreen = "locations" }
            )
            "form" -> TaskFormScreen(
                modifier = Modifier.padding(padding),
                existingTask = editingTask,
                viewModel = viewModel,
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
                onBack = { currentScreen = "list" },
                onManageLocations = { currentScreen = "locations" }
            )
            "locations" -> LocationsScreen(
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
    onBack: () -> Unit,
    onManageLocations: () -> Unit
) {
    val settings by viewModel.settings.collectAsState(initial = null)
    val currentTheme = settings?.themeName ?: "Purple"
    val locations by viewModel.allLocations.collectAsState(initial = emptyList())

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

        // Locations Section
        Text(
            text = "Locations",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = onManageLocations
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Manage Locations",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${locations.size} location${if (locations.size != 1) "s" else ""} saved",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text("→", fontSize = 20.sp)
            }
        }

        // Theme Section
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
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier,
    onTaskClick: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onManageLocations: () -> Unit
) {
    val context = LocalContext.current
    val locations by viewModel.allLocations.collectAsState(initial = emptyList())
    val filteredTasks by viewModel.filteredTasks.collectAsState()
    val selectedFilter by viewModel.selectedLocationFilter.collectAsState()
    val displayTasks = if (selectedFilter != null) filteredTasks else tasks

    var showLocationFilter by remember { mutableStateOf(false) }
    var showPermissionDeniedDialog by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            viewModel.filterByCurrentLocation()
        } else {
            showPermissionDeniedDialog = true
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Location Filter Bar
        if (locations.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (selectedFilter) {
                                null -> "All tasks"
                                "current" -> "Near me now"
                                else -> locations.find { it.id == selectedFilter }?.name ?: "Filtered"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (selectedFilter != null) {
                            Text(
                                text = "${displayTasks.size} of ${tasks.size} tasks shown",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (selectedFilter != null) {
                            IconButton(onClick = { viewModel.clearLocationFilter() }) {
                                Text("✕", fontSize = 18.sp)
                            }
                        }
                        IconButton(onClick = { showLocationFilter = true }) {
                            Text("\uD83D\uDCCD", fontSize = 18.sp)
                        }
                    }
                }
            }
        }

        if (showLocationFilter) {
            LocationFilterDialog(
                locations = locations,
                selectedFilter = selectedFilter,
                hasLocationPermission = viewModel.hasLocationPermission(),
                onFilterSelected = { filter ->
                    when (filter) {
                        "all" -> {
                            viewModel.clearLocationFilter()
                            showLocationFilter = false
                        }
                        "current" -> {
                            if (viewModel.hasLocationPermission()) {
                                viewModel.filterByCurrentLocation()
                                showLocationFilter = false
                            } else {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                                showLocationFilter = false
                            }
                        }
                        else -> {
                            viewModel.filterByLocation(filter)
                            showLocationFilter = false
                        }
                    }
                },
                onManageLocations = {
                    showLocationFilter = false
                    onManageLocations()
                },
                onDismiss = { showLocationFilter = false }
            )
        }

        if (showPermissionDeniedDialog) {
            AlertDialog(
                onDismissRequest = { showPermissionDeniedDialog = false },
                title = { Text("Location Permission Required") },
                text = { Text("To filter tasks by your current location, please grant location permission in your device settings.") },
                confirmButton = {
                    TextButton(onClick = { showPermissionDeniedDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
        if (displayTasks.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    if (tasks.isEmpty()) "No tasks yet.\nTap + to add a task!"
                    else "No tasks match the selected location filter.",
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
                items(displayTasks) { task ->
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
    viewModel: TaskViewModel,
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
    var selectedLocationId by remember { mutableStateOf(existingTask?.locationId) }
    var showLocationPicker by remember { mutableStateOf(false) }

    val locations by viewModel.allLocations.collectAsState(initial = emptyList())

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

        // Location Picker
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Location (optional)",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedButton(
                onClick = { showLocationPicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = locations.find { it.id == selectedLocationId }?.name ?: "No location selected"
                )
            }

            if (selectedLocationId != null) {
                TextButton(onClick = { selectedLocationId = null }) {
                    Text("Clear location")
                }
            }
        }

        if (showLocationPicker) {
            LocationPickerDialog(
                locations = locations,
                selectedLocationId = selectedLocationId,
                onLocationSelected = { locationId ->
                    selectedLocationId = locationId
                    showLocationPicker = false
                },
                onDismiss = { showLocationPicker = false }
            )
        }

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
                                reward = reward,
                                locationId = selectedLocationId
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

// ===== Location Components =====

@Composable
fun LocationPickerDialog(
    locations: List<Location>,
    selectedLocationId: String?,
    onLocationSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Location") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (locations.isEmpty()) {
                    Text(
                        "No locations yet. Go to Settings → Manage Locations to create one.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    locations.forEach { location ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onLocationSelected(location.id) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (location.id == selectedLocationId)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = location.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (location.id == selectedLocationId)
                                            FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(
                                        text = "Radius: ${location.radiusMeters.toInt()}m",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (location.id == selectedLocationId) {
                                    Text("✓", fontSize = 20.sp)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}


@Composable
fun LocationFilterDialog(
    locations: List<Location>,
    selectedFilter: String?,
    hasLocationPermission: Boolean,
    onFilterSelected: (String) -> Unit,
    onManageLocations: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Tasks by Location") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // All tasks option
                FilterOption(
                    label = "All tasks",
                    isSelected = selectedFilter == null,
                    onClick = { onFilterSelected("all") }
                )

                // Near me option
                Column {
                    FilterOption(
                        label = "\uD83D\uDCCD Near me now",
                        isSelected = selectedFilter == "current",
                        onClick = { onFilterSelected("current") }
                    )
                    if (!hasLocationPermission) {
                        Text(
                            text = "Requires location permission",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                // Individual locations
                if (locations.isEmpty()) {
                    Text(
                        "No saved locations yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    locations.forEach { location ->
                        FilterOption(
                            label = location.name,
                            isSelected = selectedFilter == location.id,
                            onClick = { onFilterSelected(location.id) }
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                OutlinedButton(
                    onClick = onManageLocations,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Manage Locations")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun FilterOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = label,
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
fun LocationsScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val locations by viewModel.allLocations.collectAsState(initial = emptyList())
    var showAddLocationDialog by remember { mutableStateOf(false) }
    var editingLocation by remember { mutableStateOf<Location?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("← Back")
            }

            Button(onClick = {
                editingLocation = null
                showAddLocationDialog = true
            }) {
                Text("+ Add Location")
            }
        }

        Text(
            text = "Manage Locations",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        if (locations.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    "No locations yet.\nTap 'Add Location' to create one.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(locations) { location ->
                    LocationCard(
                        location = location,
                        onEdit = {
                            editingLocation = location
                            showAddLocationDialog = true
                        },
                        onDelete = { viewModel.deleteLocation(location) }
                    )
                }
            }
        }
    }

    if (showAddLocationDialog) {
        MapLocationPickerDialog(
            existingLocation = editingLocation,
            viewModel = viewModel,
            onSave = { location ->
                if (editingLocation != null) {
                    viewModel.updateLocation(location)
                } else {
                    viewModel.insertLocation(location)
                }
                showAddLocationDialog = false
                editingLocation = null
            },
            onDismiss = {
                showAddLocationDialog = false
                editingLocation = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationCard(
    location: Location,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = location.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    IconButton(onClick = onEdit) {
                        Text("✎", fontSize = 20.sp)
                    }
                    IconButton(onClick = onDelete) {
                        Text("🗑", fontSize = 20.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoChip("Lat: %.4f".format(location.latitude))
                InfoChip("Lon: %.4f".format(location.longitude))
            }

            Spacer(modifier = Modifier.height(4.dp))

            InfoChip("Radius: ${location.radiusMeters.toInt()}m")
        }
    }
}

@Composable
fun InfoChip(text: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun AddLocationDialog(
    existingLocation: Location?,
    onSave: (Location) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(existingLocation?.name ?: "") }
    var latitude by remember { mutableStateOf(existingLocation?.latitude?.toString() ?: "") }
    var longitude by remember { mutableStateOf(existingLocation?.longitude?.toString() ?: "") }
    var radius by remember { mutableStateOf(existingLocation?.radiusMeters?.toInt()?.toString() ?: "500") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingLocation != null) "Edit Location" else "Add Location") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Location Name") },
                    placeholder = { Text("e.g., Home, Office, Gym") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    label = { Text("Latitude") },
                    placeholder = { Text("e.g., 40.7128") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = longitude,
                    onValueChange = { longitude = it },
                    label = { Text("Longitude") },
                    placeholder = { Text("e.g., -74.0060") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = radius,
                    onValueChange = { radius = it },
                    label = { Text("Radius (meters)") },
                    placeholder = { Text("e.g., 500") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Tip: Use a maps app to find GPS coordinates. Long-press on a location to see its latitude and longitude.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val lat = latitude.toDoubleOrNull()
                    val lon = longitude.toDoubleOrNull()
                    val rad = radius.toFloatOrNull()

                    if (name.isNotBlank() && lat != null && lon != null && rad != null) {
                        onSave(
                            Location(
                                id = existingLocation?.id ?: java.util.UUID.randomUUID().toString(),
                                name = name,
                                latitude = lat,
                                longitude = lon,
                                radiusMeters = rad
                            )
                        )
                    }
                },
                enabled = name.isNotBlank() &&
                        latitude.toDoubleOrNull() != null &&
                        longitude.toDoubleOrNull() != null &&
                        radius.toFloatOrNull() != null
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
