package com.tuapp.corriente.data

import com.tuapp.corriente.data.entity.Circuito

object DatosIniciales {

    val circuitos: List<Circuito> = listOf(
        Circuito(
            codigo = "A060",
            nombre = "Nueva Gerona Centro",
            zona = "Nueva Gerona",
            descripcion = "Área central desde Calle 37 hasta el río; transversal de Calle 40 a Calle 14. Incluye sectores de calles 37, 35, 33 y 31 (Calle 14 a Calle 38).",
            color = 0xFFF44336.toInt()
        ),
        Circuito(
            codigo = "A070",
            nombre = "Pueblo Nuevo / Estadio",
            zona = "Nueva Gerona",
            descripcion = "Reparto 26 de Julio, Estadio y Pueblo Nuevo. Límite en Calle 41 entre calles 26 y 34, extendiéndose hacia la loma.",
            color = 0xFF9C27B0.toInt()
        ),
        Circuito(
            codigo = "A133",
            nombre = "Pueblo Nuevo (par)",
            zona = "Nueva Gerona",
            descripcion = "Comparte bloque con A070. Reparto 26 de Julio y zona del Estadio.",
            color = 0xFF673AB7.toInt()
        ),
        Circuito(
            codigo = "A040",
            nombre = "Nueva Gerona (residencial)",
            zona = "Nueva Gerona",
            descripcion = "Parte del casco urbano y zonas residenciales intermedias. Afectaciones reportadas en Calle 45 entre 26 y 28.",
            color = 0xFF2196F3.toInt()
        ),
        Circuito(
            codigo = "A480",
            nombre = "Norte de Gerona",
            zona = "Norte",
            descripcion = "Eje norte: Chacón, Playas, Sierra Caballo y área del polígono.",
            color = 0xFF009688.toInt()
        ),
        Circuito(
            codigo = "A210",
            nombre = "Santa Fe",
            zona = "Santa Fe",
            descripcion = "Casco histórico de La Fe, reparto Camilo, Cochabamba y Carretera Júcaro hasta el Mesón (incluye unidad militar 1180).",
            color = 0xFFFF9800.toInt()
        ),
        Circuito(
            codigo = "A010",
            nombre = "Mella Cítrico / Rural",
            zona = "Mella",
            descripcion = "Ramal principal hacia el poblado Mella Cítrico y puntos periféricos al norte de Nueva Gerona.",
            color = 0xFF4CAF50.toInt()
        )
        // Pendientes de datos geográficos:
        // A147, A220, A090, A330, A340, A310
    )
}