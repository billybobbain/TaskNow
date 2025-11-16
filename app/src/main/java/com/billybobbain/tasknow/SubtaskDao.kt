// ===== SubtaskDao.kt =====
package com.billybobbain.tasknow

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SubtaskDao {
    @Query("SELECT * FROM subtasks WHERE taskId = :taskId ORDER BY orderIndex ASC")
    fun getSubtasksForTask(taskId: String): Flow<List<Subtask>>

    @Query("SELECT * FROM subtasks WHERE id = :subtaskId")
    suspend fun getSubtaskById(subtaskId: String): Subtask?

    @Query("SELECT * FROM subtasks WHERE taskId = :taskId AND isCompleted = 0 ORDER BY orderIndex ASC LIMIT 1")
    suspend fun getNextIncompleteSubtask(taskId: String): Subtask?

    @Query("SELECT COUNT(*) FROM subtasks WHERE taskId = :taskId")
    suspend fun getSubtaskCount(taskId: String): Int

    @Query("SELECT COUNT(*) FROM subtasks WHERE taskId = :taskId AND isCompleted = 1")
    suspend fun getCompletedSubtaskCount(taskId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtask(subtask: Subtask)

    @Update
    suspend fun updateSubtask(subtask: Subtask)

    @Delete
    suspend fun deleteSubtask(subtask: Subtask)

    @Query("DELETE FROM subtasks WHERE taskId = :taskId")
    suspend fun deleteSubtasksForTask(taskId: String)
}
