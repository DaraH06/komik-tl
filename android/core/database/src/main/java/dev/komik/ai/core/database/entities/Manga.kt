package dev.komik.ai.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manga")
data class Manga(
    @PrimaryKey val id: String,
    val title: String,
    val source: String,
    val uri: String,
    val addedAt: Long,
)
