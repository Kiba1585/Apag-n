package com.tuapp.corriente.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "circuitos")
data class Circuito(
    @PrimaryKey val codigo: String,
    val nombre: String,
    val zona: String,
    val descripcion: String,
    val color: Int,
    val activo: Boolean = true,
    val servicioCritico: Boolean = false,
    val tipoServicio: String = ""   // "hospital", "bombeo", "escuela", ""
)