package dev.komik.ai.core.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "translation",
    foreignKeys = [
        ForeignKey(
            entity = Page::class,
            parentColumns = ["id"],
            childColumns = ["pageId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("pageId")],
)
data class Translation(
    @PrimaryKey val id: String,
    val pageId: String,
    val targetLang: String,
    val boxId: String?,
    val text: String,
    val latencyMs: Long,
    val promptVersion: String,
)
