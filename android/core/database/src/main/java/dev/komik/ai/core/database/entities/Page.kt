package dev.komik.ai.core.database.entities

data class Page(
    val chapterId: String,
    val pageIndex: Int,
    val width: Int,
    val height: Int,
    val checksum: String,
    val cacheKey: String? = null,
    val filePath: String? = null,
)
