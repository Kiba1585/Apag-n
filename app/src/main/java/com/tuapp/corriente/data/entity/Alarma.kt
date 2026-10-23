package com.tuapp.corriente.data.entity

data class Alarma(
    val id: Long = 0,
    val circuitoCodigo: String,
    val bloqueId: Long? = null,
    val minutosAntes: Int = 15,
    val tono: String = "default",
    val vibrar: Boolean = true,
    val volumen: Int = 80,
    val activa: Boolean = true
)