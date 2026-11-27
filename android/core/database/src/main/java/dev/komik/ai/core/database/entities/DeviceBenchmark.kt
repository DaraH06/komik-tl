package dev.komik.ai.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Satu baris ("device") menyimpan hasil benchmark perangkat untuk auto-tier. */
@Entity(tableName = "device_benchmark")
data class DeviceBenchmark(
    @PrimaryKey val id: String,
    val totalRamMb: Int,
    val freeRamMbAtCapture: Int,
    val tier: String,
    val capturedAt: Long,
)
