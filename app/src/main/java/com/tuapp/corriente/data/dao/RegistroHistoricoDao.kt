package com.tuapp.corriente.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tuapp.corriente.data.entity.RegistroHistorico
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroHistoricoDao {
    @Query("SELECT * FROM historial ORDER BY inicio DESC")
    fun observarTodos(): Flow<List<RegistroHistorico>>

    @Query("SELECT * FROM historial WHERE circuitoCodigo = :codigo ORDER BY inicio DESC")
    fun observarPorCircuito(codigo: String): Flow<List<RegistroHistorico>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(registro: RegistroHistorico): Long

    @Query("DELETE FROM historial")
    suspend fun limpiar()
}