package com.tuapp.corriente.data.dao

import androidx.room.*
import com.tuapp.corriente.data.entity.RegistroHistorico
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroHistoricoDao {
    @Query("SELECT * FROM historial ORDER BY inicio DESC")
    fun observarTodos(): Flow<List<RegistroHistorico>>

    @Query("SELECT * FROM historial WHERE zonaId = :zonaId ORDER BY inicio DESC")
    fun observarPorZona(zonaId: Long): Flow<List<RegistroHistorico>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(registro: RegistroHistorico): Long

    @Query("DELETE FROM historial")
    suspend fun limpiar()
}