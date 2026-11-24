package dev.komik.ai.core.database.entities

data class JobLog(
    val jobId: String,
    val mangaId: String,
    val state: String,
    val startMs: Long,
    val endMs: Long? = null,
    val error: String? = null,
)
