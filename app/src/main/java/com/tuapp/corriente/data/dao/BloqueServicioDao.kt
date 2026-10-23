package com.tuapp.corriente.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tuapp.corriente.data.entity.BloqueServicio
import kotlinx.coroutines.flow.Flow

@Dao
interface BloqueServicioDao {

    @Query("SELECT * FROM bloques_servicio ORDER BY horaInicio, circuitoCodigo")
    fun observarTodos(): Flow<List<BloqueServicio>>

    @Query("SELECT * FROM bloques_servicio WHERE circuitoCodigo = :codigo ORDER BY horaInicio")
    fun observarPorCircuito(codigo: String): Flow<List<BloqueServicio>>

    @Query("SELECT * FROM bloques_servicio WHERE circuitoCodigo = :codigo ORDER BY horaInicio")
    suspend fun obtenerPorCircuito(codigo: String): List<BloqueServicio>

    // ESTA ES LA FUNCIÓN QUE FALTABA:
    @Query("SELECT * FROM bloques_servicio ORDER BY horaInicio")
    suspend fun obtenerTodos(): List<BloqueServicio>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(bloque: BloqueServicio): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVarios(bloques: List<BloqueServicio>)

    @Update
    suspend fun actualizar(bloque: BloqueServicio)

    @Delete
    suspend fun eliminar(bloque: BloqueServicio)

    @Query("DELETE FROM bloques_servicio WHERE circuitoCodigo = :codigo")
    suspend fun eliminarPorCircuito(codigo: String)

    @Query("DELETE FROM bloques_servicio")
    suspend fun limpiar()
}