package dev.komik.ai.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.komik.ai.core.database.entities.OCRResult

@Dao
interface OcrResultDao {
    @Query("SELECT * FROM ocr_result WHERE pageId = :pageId")
    fun forPage(pageId: String): List<OCRResult>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(result: OCRResult)

    @Query("DELETE FROM ocr_result WHERE pageId = :pageId")
    fun deleteForPage(pageId: String)
}
