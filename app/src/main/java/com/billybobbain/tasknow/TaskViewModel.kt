// ===== TaskViewModel.kt =====
package com.billybobbain.tasknow

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val taskRepository: TaskRepository
    private val settingsRepository: SettingsRepository
    val allTasks: Flow<List<Task>>
    val settings: Flow<Settings?>

    init {
        val database = TaskDatabase.getDatabase(application)
        taskRepository = TaskRepository(database.taskDao())
        settingsRepository = SettingsRepository(database.settingsDao())
        allTasks = taskRepository.allTasks
        settings = settingsRepository.settings
    }

    fun insert(task: Task) = viewModelScope.launch {
        taskRepository.insert(task)
    }

    fun update(task: Task) = viewModelScope.launch {
        taskRepository.update(task)
    }

    fun delete(task: Task) = viewModelScope.launch {
        taskRepository.delete(task)
    }

    fun saveTheme(themeName: String) = viewModelScope.launch {
        settingsRepository.saveSettings(Settings(themeName = themeName))
    }
}
