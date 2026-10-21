package com.tuapp.corriente.data.dao

import androidx.room.*
import com.tuapp.corriente.data.entity.Zona
import kotlinx.coroutines.flow.Flow

@Dao
interface ZonaDao {
    @Query("SELECT * FROM zonas ORDER BY id ASC")
    fun observarTodas(): Flow<List<Zona>>

    @Query("SELECT * FROM zonas WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Long): Zona?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(zona: Zona): Long

    @Update
    suspend fun actualizar(zona: Zona)

    @Delete
    suspend fun eliminar(zona: Zona)
}