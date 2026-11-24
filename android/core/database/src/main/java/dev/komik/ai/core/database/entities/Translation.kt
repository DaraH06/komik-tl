package dev.komik.ai.core.database.entities

data class Translation(
    val pageId: String,
    val targetLang: String,
    val boxId: String?,
    val text: String,
    val latencyMs: Long,
    val promptVersion: String,
)
