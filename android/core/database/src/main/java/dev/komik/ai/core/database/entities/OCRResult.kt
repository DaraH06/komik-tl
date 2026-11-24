package dev.komik.ai.core.database.entities

data class OCRResult(
    val pageId: String,
    val boxes: List<Box>,
    val lines: List<OcrLine>,
    val backend: String,
)

data class Box(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val kind: String,
)

data class OcrLine(
    val text: String,
    val confidence: Float,
)
