package com.tuapp.corriente.util

import com.tuapp.corriente.data.entity.ReglaHorario
import java.time.Duration
import java.time.LocalDateTime

object HorarioCalculator {

    data class Estado(
        val tieneCorriente: Boolean,
        val minutosHastaCambio: Long
    )

    fun calcular(regla: ReglaHorario?, ahora: LocalDateTime): Estado {
        if (regla == null) {
            return Estado(tieneCorriente = false, minutosHastaCambio = 0)
        }

        val minutosCiclo = (regla.horasCorriente + regla.horasApagon) * 60
        val minutosLuz = regla.horasCorriente * 60

        // Si el ciclo es 0 o negativo, evitamos división por cero
        if (minutosCiclo <= 0) return Estado(false, 0)

        // Tiempo transcurrido desde el punto de partida
        var minutosTranscurridos = Duration.between(regla.inicioAncla, ahora).toMinutes()

        // Si por alguna razón la fecha "ancla" está en el futuro, ajustamos
        while (minutosTranscurridos < 0) {
            minutosTranscurridos += minutosCiclo
        }

        // El residuo nos dice en qué minuto del ciclo estamos (0 a minutosCiclo)
        val minutoActualCiclo = minutosTranscurridos % minutosCiclo

        return if (minutoActualCiclo < minutosLuz) {
            // Estamos dentro del bloque de luz
            Estado(
                tieneCorriente = true,
                minutosHastaCambio = minutosLuz - minutoActualCiclo
            )
        } else {
            // Estamos en apagón
            Estado(
                tieneCorriente = false,
                minutosHastaCambio = minutosCiclo - minutoActualCiclo
            )
        }
    }
}