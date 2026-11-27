package dev.komik.ai.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.komik.ai.core.database.entities.ModelRegistry

@Dao
interface ModelRegistryDao {
    @Query("SELECT * FROM model_registry ORDER BY kind, id")
    fun all(): List<ModelRegistry>

    @Query("SELECT * FROM model_registry WHERE id = :id LIMIT 1")
    fun byId(id: String): ModelRegistry?

    @Query("SELECT * FROM model_registry WHERE kind = :kind")
    fun forKind(kind: String): List<ModelRegistry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(model: ModelRegistry)

    @Query("DELETE FROM model_registry WHERE id = :id")
    fun delete(id: String)
}
