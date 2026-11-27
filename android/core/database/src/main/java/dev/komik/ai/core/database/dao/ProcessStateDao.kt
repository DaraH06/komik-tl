package dev.komik.ai.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.komik.ai.core.database.entities.ProcessState

@Dao
interface ProcessStateDao {
    @Query("SELECT * FROM process_state WHERE chapterId = :chapterId LIMIT 1")
    fun forChapter(chapterId: String): ProcessState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(state: ProcessState)

    @Query("DELETE FROM process_state WHERE chapterId = :chapterId")
    fun delete(chapterId: String)
}
