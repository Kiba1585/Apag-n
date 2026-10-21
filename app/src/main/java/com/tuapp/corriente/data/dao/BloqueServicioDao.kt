package com.tuapp.corriente.data.dao

import androidx.room.*
import com.tuapp.corriente.data.entity.BloqueServicio
import kotlinx.coroutines.flow.Flow

@Dao
interface BloqueServicioDao {
    @Query("SELECT * FROM bloques_servicio WHERE circuitoCodigo = :codigo ORDER BY horaInicio")
    fun observarPorCircuito(codigo: String): Flow<List<BloqueServicio>>

    @Query("SELECT * FROM bloques_servicio WHERE circuitoCodigo = :codigo ORDER BY horaInicio")
    suspend fun obtenerPorCircuito(codigo: String): List<BloqueServicio>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVarios(bloques: List<BloqueServicio>)

    @Query("DELETE FROM bloques_servicio WHERE circuitoCodigo = :codigo")
    suspend fun eliminarPorCircuito(codigo: String)

    @Query("DELETE FROM bloques_servicio")
    suspend fun limpiar()
}