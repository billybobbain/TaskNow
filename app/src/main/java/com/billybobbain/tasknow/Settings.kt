// ===== Settings.kt =====
package com.billybobbain.tasknow

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey val id: Int = 1,
    val themeName: String = "Purple"
)