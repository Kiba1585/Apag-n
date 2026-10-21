package com.tuapp.corriente.data

import com.tuapp.corriente.data.entity.Circuito

object DatosIniciales {

    val circuitos: List<Circuito> = listOf(
        Circuito(
            codigo = "A010", nombre = "Mella Cítrico / Rural",
            zona = "Mella",
            descripcion = "Ramal principal hacia el poblado Mella Cítrico y puntos periféricos al norte de Nueva Gerona.",
            color = 0xFF4CAF50.toInt()
        ),
        Circuito(
            codigo = "A040", nombre = "Nueva Gerona (residencial)",
            zona = "Nueva Gerona",
            descripcion = "Casco urbano y residencial intermedio. Afectaciones en Calle 45 entre 26 y 28.",
            color = 0xFF2196F3.toInt()
        ),
        Circuito(
            codigo = "A050", nombre = "Pendiente de ubicación",
            zona = "Por confirmar",
            descripcion = "Circuito del Bloque 5 (11 AM – 2 PM). Falta ubicación geográfica.",
            color = 0xFF607D8B.toInt()
        ),
        Circuito(
            codigo = "A060", nombre = "Nueva Gerona Centro",
            zona = "Nueva Gerona",
            descripcion = "Área central de Calle 37 al río; transversal Calle 40 a Calle 14. Sectores calles 37, 35, 33 y 31 (Calle 14 a Calle 38).",
            color = 0xFFF44336.toInt()
        ),
        Circuito(
            codigo = "A070", nombre = "Pueblo Nuevo / Estadio",
            zona = "Nueva Gerona",
            descripcion = "Reparto 26 de Julio, Estadio y Pueblo Nuevo. Límite Calle 41 entre 26 y 34, extendiéndose a la loma.",
            color = 0xFF9C27B0.toInt()
        ),
        Circuito(
            codigo = "A090", nombre = "Reparto Industrial / Micro 70",
            zona = "Nueva Gerona",
            descripcion = "Periferia industrial: talleres, almacenes y secciones de Micro 70.",
            color = 0xFF795548.toInt()
        ),
        Circuito(
            codigo = "A133", nombre = "Pueblo Nuevo (par)",
            zona = "Nueva Gerona",
            descripcion = "Comparte bloque con A070. Reparto 26 de Julio y zona del Estadio.",
            color = 0xFF673AB7.toInt()
        ),
        Circuito(
            codigo = "A147", nombre = "La Demajagua",
            zona = "La Demajagua",
            descripcion = "Poblado de La Demajagua (Santa Bárbara). Áreas residenciales y bombeos de agua.",
            color = 0xFFE91E63.toInt()
        ),
        Circuito(
            codigo = "A210", nombre = "Santa Fe",
            zona = "Santa Fe",
            descripcion = "Casco histórico de La Fe, reparto Camilo, Cochabamba y Carretera Júcaro hasta el Mesón (incl. unidad militar 1180).",
            color = 0xFFFF9800.toInt()
        ),
        Circuito(
            codigo = "A220", nombre = "Periferia / Rural Norte NG",
            zona = "Nueva Gerona",
            descripcion = "Repartos suburbanos de Nueva Gerona, afueras y zonas agrícolas colindantes.",
            color = 0xFF00BCD4.toInt()
        ),
        Circuito(
            codigo = "A310", nombre = "Santa Fe Centro-Norte",
            zona = "Santa Fe",
            descripcion = "Casco urbano de Santa Fe: áreas comerciales centrales y escuelas aledañas.",
            color = 0xFFFFC107.toInt()
        ),
        Circuito(
            codigo = "A330", nombre = "Santa Fe – Reparto Camilo",
            zona = "Santa Fe",
            descripcion = "Reparto Camilo Cienfuegos, Cochabamba y ramales residenciales internos.",
            color = 0xFFFF5722.toInt()
        ),
        Circuito(
            codigo = "A340", nombre = "Santa Fe – Hospitales",
            zona = "Santa Fe",
            descripcion = "Eje técnico con prioridad a servicios de salud. Se extiende hacia Carretera a Júcaro.",
            color = 0xFF8BC34A.toInt(),
            servicioCritico = true,
            tipoServicio = "hospital"
        ),
        Circuito(
            codigo = "A480", nombre = "Norte de Gerona",
            zona = "Norte",
            descripcion = "Eje norte: Chacón, Playas, Sierra Caballo y área del polígono.",
            color = 0xFF009688.toInt()
        )
    )
}