package com.tuapp.corriente.data.entity

import java.time.LocalDate
import java.time.LocalTime

data class BloqueServicio(
    val id: Long = 0,
    val circuitoCodigo: String,
    val horaInicio: LocalTime,
    val horaFin: LocalTime,
    val vigenteDesde: LocalDate = LocalDate.now()
)