package dev.komik.ai.core.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ocr_result",
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
data class OCRResult(
    @PrimaryKey val id: String,
    val pageId: String,
    val boxes: List<Box>,
    val lines: List<OcrLine>,
    val backend: String,
)

/** Bukan tabel — disimpan sebagai JSON via TypeConverter. */
data class Box(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val kind: String,
)

/** Bukan tabel — disimpan sebagai JSON via TypeConverter. */
data class OcrLine(
    val text: String,
    val confidence: Float,
)
