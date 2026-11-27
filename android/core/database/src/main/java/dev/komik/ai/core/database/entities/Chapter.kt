package dev.komik.ai.core.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chapter",
    foreignKeys = [
        ForeignKey(
            entity = Manga::class,
            parentColumns = ["id"],
            childColumns = ["mangaId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("mangaId")],
)
data class Chapter(
    @PrimaryKey val id: String,
    val mangaId: String,
    val index: Int,
    val title: String,
    val url: String,
    val addedAt: Long,
)
