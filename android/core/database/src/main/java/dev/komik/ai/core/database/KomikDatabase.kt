package dev.komik.ai.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.komik.ai.core.database.dao.ChapterDao
import dev.komik.ai.core.database.dao.DeviceBenchmarkDao
import dev.komik.ai.core.database.dao.JobLogDao
import dev.komik.ai.core.database.dao.MangaDao
import dev.komik.ai.core.database.dao.ModelRegistryDao
import dev.komik.ai.core.database.dao.OcrResultDao
import dev.komik.ai.core.database.dao.PageDao
import dev.komik.ai.core.database.dao.ProcessStateDao
import dev.komik.ai.core.database.dao.ReadingProgressDao
import dev.komik.ai.core.database.dao.TranslationDao
import dev.komik.ai.core.database.entities.Chapter
import dev.komik.ai.core.database.entities.DeviceBenchmark
import dev.komik.ai.core.database.entities.JobLog
import dev.komik.ai.core.database.entities.Manga
import dev.komik.ai.core.database.entities.ModelRegistry
import dev.komik.ai.core.database.entities.OCRResult
import dev.komik.ai.core.database.entities.Page
import dev.komik.ai.core.database.entities.ProcessState
import dev.komik.ai.core.database.entities.ReadingProgress
import dev.komik.ai.core.database.entities.Translation

@Database(
    entities = [
        Manga::class,
        Chapter::class,
        Page::class,
        OCRResult::class,
        Translation::class,
        ModelRegistry::class,
        JobLog::class,
        ReadingProgress::class,
        DeviceBenchmark::class,
        ProcessState::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class KomikDatabase : RoomDatabase() {

    abstract fun mangaDao(): MangaDao
    abstract fun chapterDao(): ChapterDao
    abstract fun pageDao(): PageDao
    abstract fun ocrResultDao(): OcrResultDao
    abstract fun translationDao(): TranslationDao
    abstract fun modelRegistryDao(): ModelRegistryDao
    abstract fun jobLogDao(): JobLogDao
    abstract fun readingProgressDao(): ReadingProgressDao
    abstract fun deviceBenchmarkDao(): DeviceBenchmarkDao
    abstract fun processStateDao(): ProcessStateDao

    companion object {
        private const val DB_NAME = "komik-ai.db"

        @Volatile
        private var instance: KomikDatabase? = null

        fun get(context: Context): KomikDatabase =
            instance ?: synchronized(this) {
                instance ?: Room
                    .databaseBuilder(context.applicationContext, KomikDatabase::class.java, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
    }
}
