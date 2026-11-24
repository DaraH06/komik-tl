package dev.komik.ai.core.database.entities

data class ModelRegistry(
    val id: String,
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
