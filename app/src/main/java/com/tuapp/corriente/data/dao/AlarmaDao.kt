package com.tuapp.corriente.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tuapp.corriente.data.entity.Alarma
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmaDao {
    @Query("SELECT * FROM alarmas ORDER BY id ASC")
    fun observarTodas(): Flow<List<Alarma>>

    @Query("SELECT * FROM alarmas WHERE circuitoCodigo = :codigo")
    fun observarPorCircuito(codigo: String): Flow<List<Alarma>>

    @Query("SELECT * FROM alarmas WHERE activa = 1")
    suspend fun obtenerActivas(): List<Alarma>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(alarma: Alarma): Long

    @Update
    suspend fun actualizar(alarma: Alarma)

    @Delete
    suspend fun eliminar(alarma: Alarma)
}