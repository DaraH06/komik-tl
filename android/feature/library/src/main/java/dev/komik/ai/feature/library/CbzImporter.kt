package dev.komik.ai.feature.library

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import dev.komik.ai.core.database.KomikDatabase
import dev.komik.ai.core.database.entities.Chapter
import dev.komik.ai.core.database.entities.Manga
import dev.komik.ai.core.database.entities.Page
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.UUID
import java.util.zip.ZipInputStream

/** Hasil impor satu CBZ/ZIP. */
data class ImportSummary(
    val mangaId: String,
    val chapterId: String,
    val title: String,
    val pageCount: Int,
)

private val IMAGE_EXTS = setOf("jpg", "jpeg", "png", "webp", "gif", "bmp")

/**
 * Import satu file CBZ/ZIP lewat SAF ke `filesDir/manga/<id>/` lalu isi Room.
 * Blocking — panggil dari thread latar (Dispatchers.IO).
 */
class CbzImporter(
    private val context: Context,
    private val db: KomikDatabase,
) {
    fun importCbz(uri: Uri, title: String): Result<ImportSummary> = runCatching {
        val mangaId = UUID.randomUUID().toString()
        val chapterId = UUID.randomUUID().toString()
        val outDir = File(context.filesDir, "manga/$mangaId").apply { mkdirs() }

        data class Extracted(val orderKey: String, val file: File, val checksum: String, val width: Int, val height: Int)

        val extracted = mutableListOf<Extracted>()

        val input = context.contentResolver.openInputStream(uri)
            ?: error("Tidak bisa membuka $uri")
        input.use { raw ->
            ZipInputStream(raw.buffered()).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    val name = entry.name
                    val ext = name.substringAfterLast('.', "").lowercase()
                    if (!entry.isDirectory && ext in IMAGE_EXTS) {
                        val safeName = name.substringAfterLast('/').ifBlank { "page-${extracted.size}.$ext" }
                        val target = File(outDir, "%04d-%s".format(extracted.size, safeName))
                        val digest = MessageDigest.getInstance("SHA-256")
                        FileOutputStream(target).use { out ->
                            val buf = ByteArray(64 * 1024)
                            var read = zis.read(buf)
                            while (read != -1) {
                                out.write(buf, 0, read)
                                digest.update(buf, 0, read)
                                read = zis.read(buf)
                            }
                        }
                        val (w, h) = readBounds(target)
                        extracted += Extracted(
                            orderKey = name,
                            file = target,
                            checksum = digest.digest().toHex(),
                            width = w,
                            height = h,
                        )
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
        }

        require(extracted.isNotEmpty()) { "Tidak ada gambar di dalam arsip" }

        // urutkan natural-ish berdasarkan nama asli di arsip
        extracted.sortBy { it.orderKey }

        val now = System.currentTimeMillis()
        db.mangaDao().upsert(
            Manga(
                id = mangaId,
                title = title,
                source = "cbz",
                uri = uri.toString(),
                addedAt = now,
            ),
        )
        db.chapterDao().upsert(
            Chapter(
                id = chapterId,
                mangaId = mangaId,
                index = 0,
                title = title,
                url = uri.toString(),
                addedAt = now,
            ),
        )

        extracted.forEachIndexed { index, ex ->
            db.pageDao().upsert(
                Page(
                    id = "$chapterId:$index",
                    chapterId = chapterId,
                    pageIndex = index,
                    width = ex.width,
                    height = ex.height,
                    checksum = ex.checksum,
                    filePath = ex.file.absolutePath,
                ),
            )
        }

        ImportSummary(mangaId = mangaId, chapterId = chapterId, title = title, pageCount = extracted.size)
    }

    private fun readBounds(file: File): Pair<Int, Int> {
        val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, opts)
        return opts.outWidth.coerceAtLeast(0) to opts.outHeight.coerceAtLeast(0)
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
}
