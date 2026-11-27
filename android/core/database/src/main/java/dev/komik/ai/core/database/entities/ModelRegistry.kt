package dev.komik.ai.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "model_registry")
data class ModelRegistry(
    @PrimaryKey val id: String,
    val kind: String,
    val backend: String,
    val sha256: String,
    val bytes: Long,
    val license: String,
    val sourceLangs: List<String>,
    val targetLangs: List<String>,
    val minRamMb: Int,
    val installedAt: Long,
)
