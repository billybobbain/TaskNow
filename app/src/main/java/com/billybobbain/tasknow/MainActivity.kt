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
import androidx.compose.foundation.combinedClickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.sin
import kotlin.random.Random
import java.util.Locale

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
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text("+", fontSize = 32.sp, fontWeight = FontWeight.Bold)
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
                onSave = { task, templateSubtasks ->
                    if (editingTask != null) {
                        viewModel.update(task)
                    } else {
                        viewModel.insert(task)
                        // Create subtasks from template if provided
                        if (templateSubtasks.isNotEmpty()) {
                            templateSubtasks.forEachIndexed { index, subtaskData ->
                                viewModel.insertSubtask(
                                    Subtask(
                                        taskId = task.id,
                                        description = subtaskData.description,
                                        timeEstimate = subtaskData.timeEstimate,
                                        reward = subtaskData.reward,
                                        isCompleted = false,
                                        orderIndex = index
                                    )
                                )
                            }
                        } else if (task.subtask.isNotBlank()) {
                            // Fallback: Create single subtask from old fields
                            viewModel.insertSubtask(
                                Subtask(
                                    taskId = task.id,
                                    description = task.subtask,
                                    timeEstimate = task.timeEstimate,
                                    reward = task.reward,
                                    isCompleted = false,
                                    orderIndex = 0
                                )
                            )
                        }
                    }
                    currentScreen = "list"
                },
                onCancel = { currentScreen = "list" }
            )
            "detail" -> TaskDetailScreen(
                task = editingTask!!,
                viewModel = viewModel,
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
    val context = LocalContext.current
    var showMedicalDisclaimer by remember { mutableStateOf(false) }

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

        // Medical Disclaimer Section
        Text(
            text = "Medical Information",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = { showMedicalDisclaimer = true }
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
                        text = "View Medical Disclaimer",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Important information about medical templates",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text("⚕️", fontSize = 20.sp)
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

    // Medical Disclaimer Dialog
    if (showMedicalDisclaimer) {
        MedicalDisclaimerDialog(
            onDismiss = { showMedicalDisclaimer = false },
            onAccept = { showMedicalDisclaimer = false }
        )
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
                        locations = locations,
                        viewModel = viewModel,
                        onClick = { onTaskClick(task) },
                        onDelete = { onDeleteTask(task) }
                    )
                }
            }
        }

        Next24HoursSummary(tasks = tasks, viewModel = viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(
    task: Task,
    locations: List<Location>,
    viewModel: TaskViewModel,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val locationService = remember { LocationService(context) }

    // Get location info for this task
    val taskLocation = task.locationId?.let { locationId ->
        locations.find { it.id == locationId }
    }

    // Get subtasks for this task
    val subtasks by viewModel.getSubtasksForTask(task.id).collectAsState(initial = emptyList())
    val nextSubtask = subtasks.firstOrNull { !it.isCompleted }
    val completedCount = subtasks.count { it.isCompleted }
    val totalCount = subtasks.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    // Calculate distance if location exists
    var distanceText by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(taskLocation) {
        if (taskLocation != null && locationService.hasLocationPermission()) {
            val currentLoc = locationService.getCurrentLocation()
            if (currentLoc != null) {
                val distanceMeters = locationService.calculateDistance(
                    currentLoc.latitude,
                    currentLoc.longitude,
                    taskLocation.latitude,
                    taskLocation.longitude
                )
                val distanceMiles = distanceMeters * 0.000621371 // Convert meters to miles
                distanceText = if (distanceMiles < 0.1) {
                    "${(distanceMeters * 3.28084).toInt()} ft away"
                } else {
                    "%.1f mi away".format(distanceMiles)
                }
            }
        }
    }

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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (task.isRepeating) {
                        Text(
                            text = "↻",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (task.category != null) {
                        val categoryEnum = try {
                            TaskTemplateCategory.valueOf(task.category)
                        } catch (e: IllegalArgumentException) {
                            null
                        }
                        if (categoryEnum != null) {
                            Text(
                                text = categoryEnum.emoji,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                    Text(
                        text = task.taskName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDelete) {
                    Text("×", fontSize = 24.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Location info
            if (taskLocation != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Text("📍", fontSize = 12.sp)
                            Text(
                                text = taskLocation.name,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }

                    if (distanceText != null) {
                        Text(
                            text = distanceText!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Target days
            if (task.targetDays != null) {
                Text(
                    text = "🎯 Target: ${task.targetDays} day${if (task.targetDays != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Subtask progress
            if (totalCount > 0) {
                Text(
                    text = if (nextSubtask != null) "Next: ${nextSubtask.description}" else "All subtasks complete! ✓",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (nextSubtask != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                    fontWeight = if (nextSubtask == null) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.fillMaxWidth()
                )

                if (nextSubtask != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⏱️ ${nextSubtask.timeEstimate} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                )
            } else {
                // Fallback to old format if no subtasks (for existing tasks)
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
}

@Composable
fun TaskFormScreen(
    modifier: Modifier = Modifier,
    existingTask: Task?,
    viewModel: TaskViewModel,
    onSave: (Task, List<SubtaskTemplateData>) -> Unit,
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
    var selectedCategory by remember {
        mutableStateOf(
            existingTask?.category?.let { categoryName ->
                try {
                    TaskTemplateCategory.valueOf(categoryName)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
        )
    }
    var isRepeating by remember { mutableStateOf(existingTask?.isRepeating ?: false) }
    var targetDays by remember { mutableStateOf(existingTask?.targetDays?.toString() ?: "") }
    var showLocationPicker by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var showTemplatePicker by remember { mutableStateOf(false) }
    var showMedicalDisclaimer by remember { mutableStateOf(false) }
    var pendingTemplate by remember { mutableStateOf<TaskTemplate?>(null) }
    var pendingSubtasks by remember { mutableStateOf<List<SubtaskTemplateData>>(emptyList()) }

    val locations by viewModel.allLocations.collectAsState(initial = emptyList())
    val settings by viewModel.settings.collectAsState(initial = null)

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

        // Use Template Button (only show for new tasks)
        if (existingTask == null) {
            OutlinedButton(
                onClick = { showTemplatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("📋 Use Template")
            }
        }

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

        // Repeating Task Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Repeating Task",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Task will reset when all subtasks are complete",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = isRepeating,
                onCheckedChange = { isRepeating = it }
            )
        }

        // Target Days
        QuestionField(
            question = "Target completion (days from now, optional)",
            value = targetDays,
            onValueChange = { targetDays = it.filter { char -> char.isDigit() } },
            isNumeric = true
        )

        // Category Picker
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Category (optional)",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedButton(
                onClick = { showCategoryPicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    if (selectedCategory != null) {
                        Text(selectedCategory!!.emoji, fontSize = 18.sp)
                        Text(selectedCategory!!.displayName)
                    } else {
                        Text("No category selected")
                    }
                }
            }

            if (selectedCategory != null) {
                TextButton(onClick = { selectedCategory = null }) {
                    Text("Clear category")
                }
            }
        }

        if (showCategoryPicker) {
            CategoryPickerDialog(
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    selectedCategory = category
                    showCategoryPicker = false
                },
                onDismiss = { showCategoryPicker = false }
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
                                locationId = selectedLocationId,
                                category = selectedCategory?.name,
                                isRepeating = isRepeating,
                                targetDays = targetDays.toIntOrNull()
                            ),
                            pendingSubtasks
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

    // Template Picker Dialog
    if (showTemplatePicker) {
        TemplatePickerDialog(
            onTemplateSelected = { template ->
                // Check if it's a medical template and user hasn't seen disclaimer
                if (template.category == TaskTemplateCategory.MEDICAL &&
                    settings?.hasSeenMedicalDisclaimer == false) {
                    // Show disclaimer first
                    pendingTemplate = template
                    showMedicalDisclaimer = true
                    showTemplatePicker = false
                } else {
                    // Apply template to form fields
                    taskName = template.taskName
                    description = template.description
                    avoidanceReason = template.avoidanceReason
                    benefits = template.benefits
                    isRepeating = template.isRepeating
                    selectedCategory = template.category

                    // Store subtasks to be created when task is saved
                    pendingSubtasks = template.subtasks

                    // Fill in first subtask in old fields for backwards compatibility
                    if (template.subtasks.isNotEmpty()) {
                        val firstSubtask = template.subtasks.first()
                        subtask = firstSubtask.description
                        timeEstimate = firstSubtask.timeEstimate
                        reward = firstSubtask.reward
                    }

                    showTemplatePicker = false
                }
            },
            onDismiss = { showTemplatePicker = false }
        )
    }

    // Medical Disclaimer Dialog
    if (showMedicalDisclaimer && pendingTemplate != null) {
        MedicalDisclaimerDialog(
            onDismiss = {
                showMedicalDisclaimer = false
                pendingTemplate = null
            },
            onAccept = {
                // Mark disclaimer as seen
                viewModel.markMedicalDisclaimerSeen()

                // Apply the pending template
                val template = pendingTemplate!!
                taskName = template.taskName
                description = template.description
                avoidanceReason = template.avoidanceReason
                benefits = template.benefits
                isRepeating = template.isRepeating
                selectedCategory = template.category
                pendingSubtasks = template.subtasks

                if (template.subtasks.isNotEmpty()) {
                    val firstSubtask = template.subtasks.first()
                    subtask = firstSubtask.description
                    timeEstimate = firstSubtask.timeEstimate
                    reward = firstSubtask.reward
                }

                showMedicalDisclaimer = false
                pendingTemplate = null
            }
        )
    }
}

@Composable
fun QuestionField(
    question: String,
    value: String,
    onValueChange: (String) -> Unit,
    multiline: Boolean = false,
    isNumeric: Boolean = false
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
            maxLines = if (multiline) 5 else 1,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                capitalization = if (isNumeric) androidx.compose.ui.text.input.KeyboardCapitalization.None
                    else androidx.compose.ui.text.input.KeyboardCapitalization.Sentences,
                keyboardType = if (isNumeric) androidx.compose.ui.text.input.KeyboardType.Number
                    else androidx.compose.ui.text.input.KeyboardType.Text
            )
        )
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun TaskDetailScreen(
    task: Task,
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier,
    onEdit: () -> Unit,
    onBack: () -> Unit
) {
    val subtasks by viewModel.getSubtasksForTask(task.id).collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var showRewardDialog by remember { mutableStateOf(false) }
    var completedSubtask by remember { mutableStateOf<Subtask?>(null) }
    var showAddSubtaskDialog by remember { mutableStateOf(false) }
    var showEditSubtaskDialog by remember { mutableStateOf(false) }
    var editingSubtask by remember { mutableStateOf<Subtask?>(null) }
    var showConfetti by remember { mutableStateOf(false) }

    val lazyListState = androidx.compose.foundation.lazy.rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
        // Reorder subtasks when dragged
        val fromSubtask = subtasks[from.index]
        val toSubtask = subtasks[to.index]

        // Swap orderIndex values
        viewModel.updateSubtask(fromSubtask.copy(orderIndex = toSubtask.orderIndex))
        viewModel.updateSubtask(toSubtask.copy(orderIndex = fromSubtask.orderIndex))
    }

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

        // Subtasks Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(
                        "Subtasks",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    val completed = subtasks.count { it.isCompleted }
                    val total = subtasks.size
                    Text(
                        "$completed / $total",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                if (subtasks.isEmpty()) {
                    Text(
                        "No subtasks yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    )
                } else {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((subtasks.size * 100).dp), // Approximate height per item
                    ) {
                        items(subtasks.size, key = { index -> subtasks[index].id }) { index ->
                            ReorderableItem(reorderableState, key = subtasks[index].id) { isDragging ->
                                val item = subtasks[index]
                                val isNextIncomplete = !item.isCompleted &&
                                    subtasks.filter { !it.isCompleted }.minByOrNull { it.orderIndex } == item

                                SubtaskRow(
                                    subtask = item,
                                    isNextIncomplete = isNextIncomplete,
                                    isDragging = isDragging,
                                    onToggle = {
                                        if (!item.isCompleted) {
                                            // Completing - no dialog, just complete
                                            viewModel.completeSubtask(item.id)

                                            // Check if all subtasks are now complete and task is repeating
                                            scope.launch {
                                                delay(100) // Brief delay to ensure completion is saved
                                                val allComplete = subtasks.all { s -> s.isCompleted || s.id == item.id }
                                                if (allComplete) {
                                                    showConfetti = true
                                                    if (task.isRepeating) viewModel.resetAllSubtasks(task.id)
                                                }
                                            }
                                        } else {
                                            // Uncompleting - just toggle back
                                            viewModel.updateSubtask(item.copy(isCompleted = false))
                                        }
                                    },
                                    onLongClick = {
                                        editingSubtask = item
                                        showEditSubtaskDialog = true
                                    },
                                    dragModifier = Modifier.longPressDraggableHandle()
                                )

                                if (index < subtasks.lastIndex) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }

                // Add Subtask Button
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { showAddSubtaskDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("+ Add Another Subtask")
                }
            }
        }
    }

    // Reward Dialog
    if (showRewardDialog && completedSubtask != null) {
        RewardDialog(
            subtask = completedSubtask!!,
            onDismiss = {
                // Check if all subtasks are complete and task is repeating
                val allComplete = subtasks.all { it.isCompleted }
                if (allComplete && task.isRepeating) {
                    viewModel.resetAllSubtasks(task.id)
                }
                showRewardDialog = false
                completedSubtask = null
            }
        )
    }

    // Confetti celebration on final subtask completion
    if (showConfetti) {
        ConfettiOverlay(onFinished = { showConfetti = false })
    }

    // Add Subtask Dialog
    if (showAddSubtaskDialog) {
        AddSubtaskDialog(
            onDismiss = { showAddSubtaskDialog = false },
            onSave = { description, timeEstimate, reward ->
                val nextOrderIndex = (subtasks.maxOfOrNull { it.orderIndex } ?: -1) + 1
                viewModel.insertSubtask(
                    Subtask(
                        taskId = task.id,
                        description = description,
                        timeEstimate = timeEstimate,
                        reward = reward,
                        isCompleted = false,
                        orderIndex = nextOrderIndex
                    )
                )
                showAddSubtaskDialog = false
            }
        )
    }

    // Edit Subtask Dialog
    if (showEditSubtaskDialog && editingSubtask != null) {
        EditSubtaskDialog(
            subtask = editingSubtask!!,
            onDismiss = {
                showEditSubtaskDialog = false
                editingSubtask = null
            },
            onSave = { description, timeEstimate, reward ->
                viewModel.updateSubtask(
                    editingSubtask!!.copy(
                        description = description,
                        timeEstimate = timeEstimate,
                        reward = reward
                    )
                )
                showEditSubtaskDialog = false
                editingSubtask = null
            }
        )
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun SubtaskRow(
    subtask: Subtask,
    isNextIncomplete: Boolean,
    isDragging: Boolean = false,
    onToggle: () -> Unit,
    onLongClick: () -> Unit = {},
    dragModifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isDragging) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else if (isNextIncomplete) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isNextIncomplete) {
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)
        } else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDragging) 8.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            // Drag handle icon
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Drag to reorder",
                modifier = dragModifier
                    .padding(end = 8.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Checkbox(
                checked = subtask.isCompleted,
                onCheckedChange = { onToggle() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .combinedClickable(
                        onClick = { onToggle() },
                        onLongClick = { onLongClick() }
                    )
            ) {
                Text(
                    text = subtask.description,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (subtask.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                    color = if (subtask.isCompleted) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "⏱️ ${subtask.timeEstimate} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (subtask.isCompleted) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        }
                    )
                    Text(
                        text = "🎁 ${subtask.reward}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (subtask.isCompleted) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        }
                    )
                }
                if (isNextIncomplete) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "👉 Current step",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun RewardDialog(
    subtask: Subtask,
    onDismiss: () -> Unit
) {
    // Celebration animation for the reward card
    val scale by animateFloatAsState(
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "reward_scale"
    )

    val rewardCardColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.primaryContainer,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "reward_color"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "🎉 Subtask Complete!",
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "You completed:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = subtask.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.scale(scale),
                    colors = CardDefaults.cardColors(
                        containerColor = rewardCardColor
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Your Reward:",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "🎁 ${subtask.reward}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Awesome!")
            }
        }
    )
}

@Composable
fun AddSubtaskDialog(
    onDismiss: () -> Unit,
    onSave: (description: String, timeEstimate: String, reward: String) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var timeEstimate by remember { mutableStateOf("") }
    var reward by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add New Subtask")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Subtask Description") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    minLines = 2,
                    maxLines = 4,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.Sentences
                    )
                )
                OutlinedTextField(
                    value = timeEstimate,
                    onValueChange = { timeEstimate = it.filter { char -> char.isDigit() } },
                    label = { Text("Time Estimate (minutes)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                )
                OutlinedTextField(
                    value = reward,
                    onValueChange = { reward = it },
                    label = { Text("Reward") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.Sentences
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (description.isNotBlank() && timeEstimate.isNotBlank() && reward.isNotBlank()) {
                        onSave(description, timeEstimate, reward)
                    }
                },
                enabled = description.isNotBlank() && timeEstimate.isNotBlank() && reward.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditSubtaskDialog(
    subtask: Subtask,
    onDismiss: () -> Unit,
    onSave: (description: String, timeEstimate: String, reward: String) -> Unit
) {
    var description by remember { mutableStateOf(subtask.description) }
    var timeEstimate by remember { mutableStateOf(subtask.timeEstimate) }
    var reward by remember { mutableStateOf(subtask.reward) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Subtask")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Subtask Description") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    minLines = 2,
                    maxLines = 4,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.Sentences
                    )
                )
                OutlinedTextField(
                    value = timeEstimate,
                    onValueChange = { timeEstimate = it.filter { char -> char.isDigit() } },
                    label = { Text("Time Estimate (minutes)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                )
                OutlinedTextField(
                    value = reward,
                    onValueChange = { reward = it },
                    label = { Text("Reward") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.Sentences
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (description.isNotBlank() && timeEstimate.isNotBlank() && reward.isNotBlank()) {
                        onSave(description, timeEstimate, reward)
                    }
                },
                enabled = description.isNotBlank() && timeEstimate.isNotBlank() && reward.isNotBlank()
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
fun Next24HoursSummary(tasks: List<Task>, viewModel: TaskViewModel) {
    // Collect subtasks for all tasks
    val allSubtasksMap = tasks.associate { task ->
        task.id to viewModel.getSubtasksForTask(task.id).collectAsState(initial = emptyList()).value
    }

    // Calculate total time from incomplete subtasks
    val totalMinutes = allSubtasksMap.values.flatten()
        .filter { !it.isCompleted }
        .sumOf { it.timeEstimate.toIntOrNull() ?: 0 }

    val totalHours = totalMinutes / 60.0
    val percentageOfDay = (totalMinutes / (24.0 * 60.0) * 100).coerceAtMost(100.0)

    // Count tasks with incomplete subtasks
    val tasksWithIncompleteSubtasks = allSubtasksMap.count { (_, subtasks) ->
        subtasks.any { !it.isCompleted }
    }

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
                            String.format(Locale.US, "%.1fh", totalHours)
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
                        text = String.format(Locale.US, "%.1f%%", percentageOfDay),
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
                    text = if (tasksWithIncompleteSubtasks > 0) {
                        "$tasksWithIncompleteSubtasks task${if (tasksWithIncompleteSubtasks != 1) "s" else ""} with incomplete subtasks"
                    } else {
                        "All subtasks complete!"
                    },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatePickerDialog(
    onTemplateSelected: (TaskTemplate) -> Unit,
    onDismiss: () -> Unit
) {
    val categories = TaskTemplates.getAvailableCategories()
    var selectedCategory by remember { mutableStateOf<TaskTemplateCategory?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (selectedCategory == null) "Choose Template Category" else selectedCategory!!.displayName,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (selectedCategory == null) {
                    // Show categories
                    categories.forEach { category ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedCategory = category },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = category.emoji,
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                    Text(
                                        text = category.displayName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                // Visual template count indicator
                                val templateCount = TaskTemplates.getTemplatesByCategory(category).size
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                                    modifier = Modifier.padding(start = 44.dp) // Indent to align with text
                                ) {
                                    repeat(templateCount) {
                                        androidx.compose.foundation.Canvas(
                                            modifier = Modifier.size(6.dp)
                                        ) {
                                            drawCircle(
                                                color = androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Show templates in selected category
                    val templates = TaskTemplates.getTemplatesByCategory(selectedCategory!!)
                    templates.forEach { template ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onTemplateSelected(template)
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = template.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = template.taskName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        text = "${template.subtasks.size} steps",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                                    )
                                    if (template.isRepeating) {
                                        Text(
                                            text = "🔄 Repeating",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (selectedCategory != null) {
                TextButton(onClick = { selectedCategory = null }) {
                    Text("← Back to Categories")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun MedicalDisclaimerDialog(
    onDismiss: () -> Unit,
    onAccept: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "⚕️ Medical Disclaimer",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "This app provides task organization templates for common medical procedures.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "It is NOT medical advice and does not replace professional healthcare consultation.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "• Always consult with qualified healthcare professionals about medical decisions",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• This app does not diagnose, treat, or prevent any medical condition",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• Templates are for informational and organizational purposes only",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• Follow your doctor's specific instructions, not general templates",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• In case of medical emergency, call 911 or your local emergency services",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "By using the medical templates, you acknowledge this is an organizational tool only.",
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        },
        confirmButton = {
            Button(onClick = onAccept) {
                Text("I Understand")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CategoryPickerDialog(
    selectedCategory: TaskTemplateCategory?,
    onCategorySelected: (TaskTemplateCategory) -> Unit,
    onDismiss: () -> Unit
) {
    val categories = TaskTemplateCategory.values().sortedBy { it.displayName }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Choose Category",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = category == selectedCategory
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCategorySelected(category) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Text(
                                text = category.emoji,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = category.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ConfettiOverlay(onFinished: () -> Unit) {
    val colors = listOf(
        Color(0xFFFF6B6B), Color(0xFFFFD93D), Color(0xFF6BCB77),
        Color(0xFF4D96FF), Color(0xFFFF922B), Color(0xFFCC5DE8)
    )
    val particleCount = 60
    val particles = remember {
        List(particleCount) {
            mapOf(
                "x" to Random.nextFloat(),
                "y" to Random.nextFloat() * -0.5f,
                "size" to (6f + Random.nextFloat() * 8f),
                "speed" to (0.003f + Random.nextFloat() * 0.005f),
                "wobble" to (Random.nextFloat() * 8f),
                "wobbleSpeed" to (0.03f + Random.nextFloat() * 0.04f),
                "color" to Random.nextInt(colors.size).toFloat(),
                "rotation" to Random.nextFloat() * 360f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val tick by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(16, easing = LinearEasing)),
        label = "tick"
    )

    var elapsed by remember { mutableFloatStateOf(0f) }
    var lastTick by remember { mutableFloatStateOf(0f) }
    elapsed += (tick - lastTick).let { if (it < 0) 1f + it else it } * 16f
    lastTick = tick

    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        delay(2800)
        onFinished()
    }

    val alpha = if (elapsed > 2000f) 1f - ((elapsed - 2000f) / 800f).coerceIn(0f, 1f) else 1f

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            particles.forEach { p ->
                val progress = ((elapsed * (p["speed"] as Float)) % 1.5f).coerceIn(0f, 1.5f)
                val x = (p["x"] as Float) * size.width +
                        sin(elapsed * (p["wobbleSpeed"] as Float)) * (p["wobble"] as Float)
                val y = (p["y"] as Float + progress) * size.height
                if (y < 0 || y > size.height) return@forEach
                val color = colors[(p["color"] as Float).toInt()].copy(alpha = alpha)
                val sz = p["size"] as Float
                drawRect(
                    color = color,
                    topLeft = Offset(x, y),
                    size = Size(sz, sz * 0.5f)
                )
            }
        }
    }
}

