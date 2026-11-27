package dev.komik.ai.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "job_log")
data class JobLog(
    @PrimaryKey val jobId: String,
    val mangaId: String,
    val state: String,
    val startMs: Long,
    val endMs: Long? = null,
    val error: String? = null,
)
