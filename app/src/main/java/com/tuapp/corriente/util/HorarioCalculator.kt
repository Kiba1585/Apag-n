package com.tuapp.corriente.util

import com.tuapp.corriente.data.entity.BloqueServicio
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

object HorarioCalculator {

    data class Estado(
        val tieneCorriente: Boolean,
        val minutosHastaCambio: Long,
        val proximoBloque: BloqueServicio?
    )

    /**
     * Dado un circuito, sus bloques y una hora, dice si tiene corriente
     * y cuántos minutos faltan para el próximo cambio.
     */
    fun calcular(
        bloquesDelCircuito: List<BloqueServicio>,
        ahora: LocalTime
    ): Estado {
        val ordenados = bloquesDelCircuito.sortedBy { it.horaInicio }

        // ¿Está dentro de algún bloque?
        val actual = ordenados.firstOrNull { estaDentro(ahora, it) }
        if (actual != null) {
            return Estado(
                tieneCorriente = true,
                minutosHastaCambio = minutosHastaFin(ahora, actual),
                proximoBloque = siguienteBloque(ordenados, ahora)
            )
        }

        // No tiene corriente: buscar el próximo
        val proximo = siguienteBloque(ordenados, ahora)
        val minutos = proximo?.let { minutosHastaInicio(ahora, it.horaInicio) } ?: 0L
        return Estado(
            tieneCorriente = false,
            minutosHastaCambio = minutos,
            proximoBloque = proximo
        )
    }

    // ---------- Helpers ----------

    private fun estaDentro(ahora: LocalTime, b: BloqueServicio): Boolean {
        val a = aMinutos(ahora)
        val i = aMinutos(b.horaInicio)
        val f = aMinutos(b.horaFin)
        return if (i <= f) a in i until f
        else a >= i || a < f      // cruza medianoche
    }

    private fun minutosHastaFin(ahora: LocalTime, b: BloqueServicio): Long {
        val a = aMinutos(ahora)
        val f = aMinutos(b.horaFin)
        return if (f > a) (f - a).toLong() else (1440 - a + f).toLong()
    }

    private fun minutosHastaInicio(ahora: LocalTime, inicio: LocalTime): Long {
        val a = aMinutos(ahora)
        val i = aMinutos(inicio)
        return if (i > a) (i - a).toLong() else (1440 - a + i).toLong()
    }

    private fun siguienteBloque(
        ordenados: List<BloqueServicio>,
        ahora: LocalTime
    ): BloqueServicio? {
        val a = aMinutos(ahora)
        // Primero busca uno cuyo inicio sea mayor a la hora actual
        ordenados.firstOrNull { aMinutos(it.horaInicio) > a }?.let { return it }
        // Si no, el siguiente es el primero del día (ciclo)
        return ordenados.firstOrNull()
    }

    private fun aMinutos(t: LocalTime) = t.hour * 60 + t.minute

    // Utilidad para el widget: formatea "HH:mm"
    fun formatear(t: LocalTime): String =
        "%02d:%02d".format(t.hour, t.minute)
}