package com.tuapp.corriente.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tuapp.corriente.data.entity.Circuito
import kotlinx.coroutines.flow.Flow

@Dao
interface CircuitoDao {
    @Query("SELECT * FROM circuitos ORDER BY zona, codigo")
    fun observarTodos(): Flow<List<Circuito>>

    @Query("SELECT * FROM circuitos ORDER BY zona, codigo")
    suspend fun obtenerTodos(): List<Circuito>

    @Query("SELECT * FROM circuitos WHERE zona = :zona ORDER BY codigo")
    fun observarPorZona(zona: String): Flow<List<Circuito>>

    @Query("SELECT * FROM circuitos WHERE codigo = :codigo LIMIT 1")
    suspend fun obtenerPorCodigo(codigo: String): Circuito?

    @Query("SELECT COUNT(*) FROM circuitos")
    suspend fun contar(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(circuito: Circuito)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVarios(circuitos: List<Circuito>)

    @Update
    suspend fun actualizar(circuito: Circuito)

    @Delete
    suspend fun eliminar(circuito: Circuito)
}