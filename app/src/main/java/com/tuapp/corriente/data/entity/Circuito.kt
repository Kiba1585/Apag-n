package com.tuapp.corriente.data.entity

data class Circuito(
    val codigo: String,
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