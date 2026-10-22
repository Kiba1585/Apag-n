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

    val tieneSalud: Boolean = false,
    val tieneBombeo: Boolean = false,
    val tieneEducacion: Boolean = false,
    val tieneTelecom: Boolean = false,

    val prioridadMaxima: Boolean = false
)