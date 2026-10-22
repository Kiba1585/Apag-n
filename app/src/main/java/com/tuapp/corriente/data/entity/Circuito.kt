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

    // Servicios críticos que dependen del circuito
    val tieneSalud: Boolean = false,
    val tieneBombeo: Boolean = false,
    val tieneEducacion: Boolean = false,
    val tieneTelecom: Boolean = false,

    // Prioridad máxima (solo A080 - Hospital Héroes del Baire)
    val prioridadMaxima: Boolean = false
)