package dev.komik.ai.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.komik.ai.core.database.entities.DeviceBenchmark

@Dao
interface DeviceBenchmarkDao {
    @Query("SELECT * FROM device_benchmark WHERE id = 'device' LIMIT 1")
    fun get(): DeviceBenchmark?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(benchmark: DeviceBenchmark)
}
