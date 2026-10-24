package com.tuapp.corriente.data.entity

import java.time.LocalDateTime

data class ReglaHorario(
    val id: Long = 0,
    val circuitoCodigo: String,
    val inicioAncla: LocalDateTime, // Un momento conocido en el que empezó la luz
    val horasCorriente: Long,       // Ej: 3
    val horasApagon: Long           // Ej: 12
)