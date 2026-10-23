package com.tuapp.corriente.data.entity

import java.time.Instant

data class RegistroHistorico(
    val id: Long = 0,
    val circuitoCodigo: String,
    val inicio: Instant,
    val fin: Instant? = null,
    val tipo: String
)