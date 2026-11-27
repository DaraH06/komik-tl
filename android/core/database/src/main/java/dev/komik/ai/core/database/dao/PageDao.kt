package dev.komik.ai.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.komik.ai.core.database.entities.Page

@Dao
interface PageDao {
    @Query("SELECT * FROM page WHERE chapterId = :chapterId ORDER BY pageIndex ASC")
    fun forChapter(chapterId: String): List<Page>

    @Query("SELECT * FROM page WHERE id = :id LIMIT 1")
    fun byId(id: String): Page?

    @Query("SELECT COUNT(*) FROM page WHERE chapterId = :chapterId")
    fun countForChapter(chapterId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(page: Page)

    @Query("DELETE FROM page WHERE chapterId = :chapterId")
    fun deleteForChapter(chapterId: String)
}
