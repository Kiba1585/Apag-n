package com.tuapp.corriente.data

import com.tuapp.corriente.data.entity.BloqueServicio
import java.time.LocalTime

/**
 * Ciclo fijo de 8 bloques de 3 horas.
 * Cada circuito se asigna a los bloques en los que TIENE CORRIENTE.
 */
object DatosBloques {

    private val B1i = LocalTime.of(23, 0); private val B1f = LocalTime.of(2, 0)   // cruza medianoche
    private val B2i = LocalTime.of(2, 0);  private val B2f = LocalTime.of(5, 0)
    private val B3i = LocalTime.of(5, 0);  private val B3f = LocalTime.of(8, 0)
    private val B4i = LocalTime.of(8, 0);  private val B4f = LocalTime.of(11, 0)
    private val B5i = LocalTime.of(11, 0); private val B5f = LocalTime.of(14, 0)
    private val B6i = LocalTime.of(14, 0); private val B6f = LocalTime.of(17, 0)
    private val B7i = LocalTime.of(17, 0); private val B7f = LocalTime.of(20, 0)
    private val B8i = LocalTime.of(20, 0); private val B8f = LocalTime.of(23, 0)

    /** Mapa: código de circuito → lista de bloques en los que tiene corriente */
    private val asignaciones: Map<String, List<Pair<LocalTime, LocalTime>>> = mapOf(
        // Bloque 1 (23:00–02:00) y Bloque 6 (14:00–17:00)
        "A147" to listOf(B1i to B1f, B6i to B6f),
        "A220" to listOf(B1i to B1f, B6i to B6f),

        // Bloque 2 (02:00–05:00) y Bloque 7 (17:00–20:00)
        "A040" to listOf(B2i to B2f, B7i to B7f),
        "A060" to listOf(B2i to B2f, B7i to B7f),

        // Bloque 3 (05:00–08:00) y Bloque 7 (17:00–20:00)
        "A480" to listOf(B3i to B3f, B7i to B7f),
        "A090" to listOf(B3i to B3f, B7i to B7f),

        // Bloque 4 (08:00–11:00) y Bloque 8 (20:00–23:00)
        "A070" to listOf(B4i to B4f, B8i to B8f),
        "A133" to listOf(B4i to B4f, B8i to B8f),
        "A010" to listOf(B4i to B4f, B8i to B8f),
        "A330" to listOf(B4i to B4f, B8i to B8f),
        "A340" to listOf(B4i to B4f, B8i to B8f),
        "A310" to listOf(B4i to B4f, B8i to B8f),

        // Bloque 5 (11:00–14:00)
        "A050" to listOf(B5i to B5f),
        "A210" to listOf(B5i to B5f)
    )

    /** Genera la lista de BloqueServicio para insertar en la BD */
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