package dev.komik.ai.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.komik.ai.core.database.entities.JobLog

@Dao
interface JobLogDao {
    @Query("SELECT * FROM job_log WHERE mangaId = :mangaId ORDER BY startMs DESC")
    fun forManga(mangaId: String): List<JobLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(job: JobLog)

    @Query("UPDATE job_log SET state = :state, endMs = :endMs, error = :error WHERE jobId = :jobId")
    fun finish(jobId: String, state: String, endMs: Long, error: String?)
}
