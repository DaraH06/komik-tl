package dev.komik.ai.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.komik.ai.core.database.entities.Manga

@Dao
interface MangaDao {
    @Query("SELECT * FROM manga ORDER BY addedAt DESC")
    fun observeAll(): List<Manga>

    @Query("SELECT * FROM manga WHERE id = :id LIMIT 1")
    fun byId(id: String): Manga?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(manga: Manga)

    @Delete
    fun delete(manga: Manga)
}
