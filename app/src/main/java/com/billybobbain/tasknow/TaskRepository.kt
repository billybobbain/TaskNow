// ===== TaskRepository.kt =====
package com.billybobbain.tasknow

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun insert(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun update(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun delete(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun getTaskById(id: String): Task? {
        return taskDao.getTaskById(id)
    }
}

class SettingsRepository(private val settingsDao: SettingsDao) {
    val settings: Flow<Settings?> = settingsDao.getSettings()

    suspend fun saveSettings(settings: Settings) {
        settingsDao.saveSettings(settings)
    }
}

class LocationRepository(private val locationDao: LocationDao) {
    val allLocations: Flow<List<Location>> = locationDao.getAllLocations()

    suspend fun insert(location: Location) {
        locationDao.insertLocation(location)
    }

    suspend fun update(location: Location) {
        locationDao.updateLocation(location)
    }

    suspend fun delete(location: Location) {
        locationDao.deleteLocation(location)
    }

    suspend fun getLocationById(id: String): Location? {
        return locationDao.getLocationById(id)
    }
}

class SubtaskRepository(private val subtaskDao: SubtaskDao) {
    fun getSubtasksForTask(taskId: String): Flow<List<Subtask>> {
        return subtaskDao.getSubtasksForTask(taskId)
    }

    suspend fun getNextIncompleteSubtask(taskId: String): Subtask? {
        return subtaskDao.getNextIncompleteSubtask(taskId)
    }

    suspend fun getSubtaskCount(taskId: String): Int {
        return subtaskDao.getSubtaskCount(taskId)
    }

    suspend fun getCompletedSubtaskCount(taskId: String): Int {
        return subtaskDao.getCompletedSubtaskCount(taskId)
    }

    suspend fun insert(subtask: Subtask) {
        subtaskDao.insertSubtask(subtask)
    }

    suspend fun update(subtask: Subtask) {
        subtaskDao.updateSubtask(subtask)
    }

    suspend fun delete(subtask: Subtask) {
        subtaskDao.deleteSubtask(subtask)
    }

    suspend fun completeSubtask(subtaskId: String) {
        val subtask = subtaskDao.getSubtaskById(subtaskId)
        subtask?.let {
            subtaskDao.updateSubtask(it.copy(isCompleted = true))
        }
    }
}
