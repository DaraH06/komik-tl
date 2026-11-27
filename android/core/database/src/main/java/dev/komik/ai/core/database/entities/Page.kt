package dev.komik.ai.core.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "page",
    foreignKeys = [
        ForeignKey(
            entity = Chapter::class,
            parentColumns = ["id"],
            childColumns = ["chapterId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("chapterId"), Index(value = ["chapterId", "pageIndex"], unique = true)],
)
data class Page(
    @PrimaryKey val id: String,
    val chapterId: String,
    val pageIndex: Int,
    val width: Int,
    val height: Int,
    val checksum: String,
    val cacheKey: String? = null,
    val filePath: String? = null,
)
