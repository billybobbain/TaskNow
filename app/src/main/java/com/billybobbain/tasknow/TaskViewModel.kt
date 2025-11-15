// ===== TaskViewModel.kt =====
package com.billybobbain.tasknow

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val taskRepository: TaskRepository
    private val settingsRepository: SettingsRepository
    private val locationRepository: LocationRepository
    private val locationService: LocationService

    val allTasks: Flow<List<Task>>
    val settings: Flow<Settings?>
    val allLocations: Flow<List<Location>>

    // Filter state
    private val _selectedLocationFilter = MutableStateFlow<String?>(null) // null = no filter, "current" = GPS, or locationId
    val selectedLocationFilter: StateFlow<String?> = _selectedLocationFilter

    // Filtered tasks based on location
    private val _filteredTasks = MutableStateFlow<List<Task>>(emptyList())
    val filteredTasks: StateFlow<List<Task>> = _filteredTasks

    init {
        val database = TaskDatabase.getDatabase(application)
        taskRepository = TaskRepository(database.taskDao())
        settingsRepository = SettingsRepository(database.settingsDao())
        locationRepository = LocationRepository(database.locationDao())
        locationService = LocationService(application)

        allTasks = taskRepository.allTasks
        settings = settingsRepository.settings
        allLocations = locationRepository.allLocations

        // Update filtered tasks when tasks, locations, or filter changes
        viewModelScope.launch {
            combine(allTasks, allLocations, selectedLocationFilter) { tasks, locations, filter ->
                Triple(tasks, locations, filter)
            }.collect { (tasks, locations, filter) ->
                _filteredTasks.value = when (filter) {
                    null -> tasks // No filter, show all
                    "current" -> locationService.filterTasksByCurrentLocation(tasks, locations)
                    else -> locationService.filterTasksByLocation(tasks, filter)
                }
            }
        }
    }

    // Task operations
    fun insert(task: Task) = viewModelScope.launch {
        taskRepository.insert(task)
    }

    fun update(task: Task) = viewModelScope.launch {
        taskRepository.update(task)
    }

    fun delete(task: Task) = viewModelScope.launch {
        taskRepository.delete(task)
    }

    // Location operations
    fun insertLocation(location: Location) = viewModelScope.launch {
        locationRepository.insert(location)
    }

    fun updateLocation(location: Location) = viewModelScope.launch {
        locationRepository.update(location)
    }

    fun deleteLocation(location: Location) = viewModelScope.launch {
        locationRepository.delete(location)
    }

    // Filter operations
    fun setLocationFilter(filter: String?) {
        _selectedLocationFilter.value = filter
    }

    fun filterByCurrentLocation() {
        _selectedLocationFilter.value = "current"
    }

    fun filterByLocation(locationId: String) {
        _selectedLocationFilter.value = locationId
    }

    fun clearLocationFilter() {
        _selectedLocationFilter.value = null
    }

    fun hasLocationPermission(): Boolean {
        return locationService.hasLocationPermission()
    }

    // Settings
    fun saveTheme(themeName: String) = viewModelScope.launch {
        settingsRepository.saveSettings(Settings(themeName = themeName))
    }
}
