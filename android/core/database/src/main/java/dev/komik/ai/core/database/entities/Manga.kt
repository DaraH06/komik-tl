package dev.komik.ai.core.database.entities

data class Manga(
    val id: String,
    val title: String,
    val source: String,
    val uri: String,
    val addedAt: Long,
)
