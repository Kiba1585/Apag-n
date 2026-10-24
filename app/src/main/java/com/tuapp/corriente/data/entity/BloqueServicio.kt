package com.tuapp.corriente.data.entity

import java.time.LocalDateTime

data class BloqueServicio(
    val id: Long = 0,
    val circuitoCodigo: String,
    val inicio: LocalDateTime,
    val fin: LocalDateTime
)