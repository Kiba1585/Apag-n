package com.tuapp.corriente.data

import com.tuapp.corriente.data.entity.BloqueServicio
import java.time.LocalTime

/**
 * Ciclo fijo de 8 bloques de 3 horas.
 * Aquí se listan los bloques en los que CADA circuito TIENE CORRIENTE.
 *
 * Bloques:
 *   B1 23:00–02:00
 *   B2 02:00–05:00
 *   B3 05:00–08:00
 *   B4 08:00–11:00
 *   B5 11:00–14:00
 *   B6 14:00–17:00  (repite B1)
 *   B7 17:00–20:00  (repite B2+B3)
 *   B8 20:00–23:00  (repite B4)
 */
object DatosBloques {

    private val B1 = LocalTime.of(23, 0) to LocalTime.of(2, 0)   // cruza medianoche
    private val B2 = LocalTime.of(2, 0)  to LocalTime.of(5, 0)
    private val B3 = LocalTime.of(5, 0)  to LocalTime.of(8, 0)
    private val B4 = LocalTime.of(8, 0)  to LocalTime.of(11, 0)
    private val B5 = LocalTime.of(11, 0) to LocalTime.of(14, 0)
    private val B6 = LocalTime.of(14, 0) to LocalTime.of(17, 0)
    private val B7 = LocalTime.of(17, 0) to LocalTime.of(20, 0)
    private val B8 = LocalTime.of(20, 0) to LocalTime.of(23, 0)

    /** Código de circuito → lista de bloques con corriente */
    private val asignaciones: Map<String, List<Pair<LocalTime, LocalTime>>> = mapOf(

        // B1 + B6
        "A147" to listOf(B1, B6),
        "A220" to listOf(B1, B6),

        // B2 + B7 (más segundo turno madrugada para A050 y A210)
        "A040" to listOf(B2, B7),
        "A060" to listOf(B2, B7),
        "A050" to listOf(B2, B5),   // 2 turnos
        "A210" to listOf(B2, B5),   // 2 turnos

        // B3 + B7
        "A480" to listOf(B3, B7),
        "A090" to listOf(B3, B7),

        // B4 + B8
        "A070" to listOf(B4, B8),
        "A133" to listOf(B4, B8),
        "A010" to listOf(B4, B8),
        "A330" to listOf(B4, B8),
        "A340" to listOf(B4, B8),
        "A310" to listOf(B4, B8),

        // A080 (Soterrado) → TODOS los bloques MENOS B2
        "A080" to listOf(B1, B3, B4, B5, B6, B7, B8)
    )

    fun generarBloques(): List<BloqueServicio> {
        val out = mutableListOf<BloqueServicio>()
        asignaciones.forEach { (codigo, rangos) ->
            rangos.forEach { (inicio, fin) ->
                out += BloqueServicio(
                    circuitoCodigo = codigo,
                    horaInicio = inicio,
                    horaFin = fin
                )
            }
        }
        return out
    }
}