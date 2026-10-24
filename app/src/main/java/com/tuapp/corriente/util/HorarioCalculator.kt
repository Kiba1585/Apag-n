package com.tuapp.corriente.util

import com.tuapp.corriente.data.entity.BloqueServicio
import java.time.Duration
import java.time.LocalDateTime

object HorarioCalculator {

    data class Estado(
        val tieneCorriente: Boolean,
        val minutosHastaCambio: Long,
        val proximoBloque: BloqueServicio?
    )

    fun calcular(
        bloquesDelCircuito: List<BloqueServicio>,
        ahora: LocalDateTime
    ): Estado {
        val ordenados = bloquesDelCircuito.sortedBy { it.inicio }

        // 1. ¿Está dentro de algún bloque ahora mismo?
        val actual = ordenados.firstOrNull { 
            !ahora.isBefore(it.inicio) && ahora.isBefore(it.fin) 
        }
        
        if (actual != null) {
            val minutosRestantes = Duration.between(ahora, actual.fin).toMinutes()
            return Estado(
                tieneCorriente = true,
                minutosHastaCambio = minutosRestantes,
                proximoBloque = siguienteBloque(ordenados, ahora)
            )
        }

        // 2. Si no tiene corriente, buscar el próximo bloque futuro
        val proximo = siguienteBloque(ordenados, ahora)
        val minutos = proximo?.let { Duration.between(ahora, it.inicio).toMinutes() } ?: 0L
        return Estado(
            tieneCorriente = false,
            minutosHastaCambio = minutos,
            proximoBloque = proximo
        )
    }

    private fun siguienteBloque(
        ordenados: List<BloqueServicio>,
        ahora: LocalDateTime
    ): BloqueServicio? {
        return ordenados.firstOrNull { it.inicio.isAfter(ahora) }
    }
}