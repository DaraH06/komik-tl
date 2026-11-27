package dev.komik.ai.core.database

import androidx.room.TypeConverter
import dev.komik.ai.core.database.entities.Box
import dev.komik.ai.core.database.entities.OcrLine
import org.json.JSONArray
import org.json.JSONObject

/** Room tidak mendukung List<T>; simpan sebagai JSON. */
class Converters {

    // ---- List<String> ----
    @TypeConverter
    fun stringListToJson(value: List<String>): String = JSONArray(value).toString()

    @TypeConverter
    fun jsonToStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        val arr = runCatching { JSONArray(value) }.getOrNull() ?: return emptyList()
        return (0 until arr.length()).map { arr.optString(it) }
    }

    // ---- List<Box> ----
    @TypeConverter
    fun boxesToJson(value: List<Box>): String = JSONArray().apply {
        value.forEach { b ->
            put(
                JSONObject()
                    .put("x", b.x)
                    .put("y", b.y)
                    .put("width", b.width)
                    .put("height", b.height)
                    .put("kind", b.kind),
            )
        }
    }.toString()

    @TypeConverter
    fun jsonToBoxes(value: String?): List<Box> {
        if (value.isNullOrEmpty()) return emptyList()
        val arr = runCatching { JSONArray(value) }.getOrNull() ?: return emptyList()
        return (0 until arr.length()).mapNotNull { i ->
            val o = arr.optJSONObject(i) ?: return@mapNotNull null
            Box(
                x = o.optInt("x"),
                y = o.optInt("y"),
                width = o.optInt("width"),
                height = o.optInt("height"),
                kind = o.optString("kind"),
            )
        }
    }

    // ---- List<OcrLine> ----
    @TypeConverter
    fun ocrLinesToJson(value: List<OcrLine>): String = JSONArray().apply {
        value.forEach { l ->
            put(JSONObject().put("text", l.text).put("confidence", l.confidence.toDouble()))
        }
    }.toString()

    @TypeConverter
    fun jsonToOcrLines(value: String?): List<OcrLine> {
        if (value.isNullOrEmpty()) return emptyList()
        val arr = runCatching { JSONArray(value) }.getOrNull() ?: return emptyList()
        return (0 until arr.length()).mapNotNull { i ->
            val o = arr.optJSONObject(i) ?: return@mapNotNull null
            OcrLine(text = o.optString("text"), confidence = o.optDouble("confidence").toFloat())
        }
    }
}
