package dev.komik.ai.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.komik.ai.core.database.entities.ReadingProgress

@Dao
interface ReadingProgressDao {
    @Query("SELECT * FROM reading_progress WHERE chapterId = :chapterId LIMIT 1")
    fun forChapter(chapterId: String): ReadingProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(progress: ReadingProgress)
}
