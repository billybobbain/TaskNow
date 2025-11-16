// ===== Subtask.kt =====
package com.billybobbain.tasknow

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "subtasks",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("taskId")]
)
data class Subtask(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val taskId: String,
    val description: String,
    val timeEstimate: String,
    val reward: String,
    val isCompleted: Boolean = false,
    val orderIndex: Int = 0
)
