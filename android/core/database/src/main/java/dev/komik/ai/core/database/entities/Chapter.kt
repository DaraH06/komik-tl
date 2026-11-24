package dev.komik.ai.core.database.entities

data class Chapter(
    val id: String,
    val mangaId: String,
    val index: Int,
    val title: String,
    val url: String,
    val addedAt: Long,
)
