package dev.komik.ai.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.komik.ai.core.database.entities.Translation

@Dao
interface TranslationDao {
    @Query("SELECT * FROM translation WHERE pageId = :pageId AND targetLang = :targetLang")
    fun forPage(pageId: String, targetLang: String): List<Translation>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(translation: Translation)

    @Query("DELETE FROM translation WHERE pageId = :pageId")
    fun deleteForPage(pageId: String)
}
