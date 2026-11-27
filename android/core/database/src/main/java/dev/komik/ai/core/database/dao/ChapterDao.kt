package dev.komik.ai.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.komik.ai.core.database.entities.Chapter

@Dao
interface ChapterDao {
    @Query("SELECT * FROM chapter WHERE mangaId = :mangaId ORDER BY `index` ASC")
    fun forManga(mangaId: String): List<Chapter>

    @Query("SELECT * FROM chapter WHERE id = :id LIMIT 1")
    fun byId(id: String): Chapter?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(chapter: Chapter)

    @Query("DELETE FROM chapter WHERE mangaId = :mangaId")
    fun deleteForManga(mangaId: String)
}
