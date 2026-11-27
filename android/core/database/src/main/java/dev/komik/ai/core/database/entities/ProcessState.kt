package dev.komik.ai.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Status pemrosesan per chapter (WorkManager / engine progress). */
@Entity(tableName = "process_state")
data class ProcessState(
    @PrimaryKey val chapterId: String,
    val state: String,
    val stage: String? = null,
    val progressDone: Int = 0,
    val progressTotal: Int = 0,
    val updatedAt: Long,
    val error: String? = null,
)
