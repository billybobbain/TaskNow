// ===== Task.kt =====
package com.billybobbain.tasknow

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val taskName: String,
    val description: String,
    val avoidanceReason: String,
    val benefits: String,
    val subtask: String,
    val timeEstimate: String,
    val reward: String
)