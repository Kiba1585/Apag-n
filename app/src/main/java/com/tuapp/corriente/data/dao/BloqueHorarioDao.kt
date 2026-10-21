package com.tuapp.corriente.data.dao

import androidx.room.*
import com.tuapp.corriente.data.entity.BloqueHorario
import kotlinx.coroutines.flow.Flow

@Dao
interface BloqueHorarioDao {
    @Query("SELECT * FROM bloques_horario WHERE zonaId = :zonaId AND diaSemana = :dia ORDER BY horaInicio ASC")
    fun observarPorZonaYDia(zonaId: Long, dia: Int): Flow<List<BloqueHorario>>

    @Query("SELECT * FROM bloques_horario WHERE zonaId = :zonaId ORDER BY diaSemana, horaInicio")
    suspend fun obtenerPorZona(zonaId: Long): List<BloqueHorario>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(bloque: BloqueHorario): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVarios(bloques: List<BloqueHorario>)

    @Update
    suspend fun actualizar(bloque: BloqueHorario)

    @Delete
    suspend fun eliminar(bloque: BloqueHorario)

    @Query("DELETE FROM bloques_horario WHERE zonaId = :zonaId")
    suspend fun eliminarPorZona(zonaId: Long)
}
