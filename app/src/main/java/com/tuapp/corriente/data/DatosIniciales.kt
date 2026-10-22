package com.tuapp.corriente.data

import com.tuapp.corriente.data.entity.Circuito

object DatosIniciales {

    val circuitos: List<Circuito> = listOf(

        Circuito(
            codigo = "A010", nombre = "Mella Cítrico / Rural",
            zona = "Mella",
            descripcion = "Ramal principal hacia el poblado Mella Cítrico y puntos periféricos al norte de Nueva Gerona. " +
                "Da servicio a consultorios médicos rurales, pozos agrícolas y escuelas primarias.",
            color = 0xFF4CAF50.toInt(),
            tieneSalud = true, tieneBombeo = true, tieneEducacion = true
        ),

        Circuito(
            codigo = "A040", nombre = "Nueva Gerona (residencial)",
            zona = "Nueva Gerona",
            descripcion = "Casco urbano y residencial intermedio. Afectaciones reportadas en Calle 45 entre 26 y 28. " +
                "Sirve a consultorios urbanos, politécnicos y secundarias básicas.",
            color = 0xFF2196F3.toInt(),
            tieneSalud = true, tieneEducacion = true
        ),

        Circuito(
            codigo = "A050", nombre = "Reparto Abel Santamaría",
            zona = "Nueva Gerona",
            descripcion = "Zona residencial de alta densidad poblacional. Consultorios y escuelas primarias del reparto.",
            color = 0xFF607D8B.toInt(),
            tieneSalud = true, tieneEducacion = true
        ),

        Circuito(
            codigo = "A060", nombre = "Nueva Gerona Centro",
            zona = "Nueva Gerona",
            descripcion = "Área central de Calle 37 al río; transversal Calle 40 a Calle 14. " +
                "Sectores calles 37, 35, 33 y 31 (Calle 14 a Calle 38). Incluye rebombeo menor urbano.",
            color = 0xFFF44336.toInt(),
            tieneBombeo = true
        ),

        Circuito(
            codigo = "A070", nombre = "Pueblo Nuevo / Estadio",
            zona = "Nueva Gerona",
            descripcion = "Reparto 26 de Julio, Estadio y Pueblo Nuevo. Límite Calle 41 entre 26 y 34. " +
                "Alimenta el Policlínico Juan Manuel Páez Inchausti, sedes universitarias y el telecentro Islavisión.",
            color = 0xFF9C27B0.toInt(),
            tieneSalud = true, tieneEducacion = true, tieneTelecom = true
        ),

        Circuito(
            codigo = "A080", nombre = "Soterrado – Hospital Héroes del Baire",
            zona = "Nueva Gerona",
            descripcion = "Línea de máxima prioridad. Alimenta directamente al Hospital General Docente Héroes del Baire. " +
                "Única afectación programada: 02:00 AM – 05:00 AM (el hospital pasa a generadores diésel).",
            color = 0xFF0D47A1.toInt(),
            tieneSalud = true, tieneBombeo = true,
            prioridadMaxima = true
        ),

        Circuito(
            codigo = "A090", nombre = "Reparto Industrial / Micro 70",
            zona = "Nueva Gerona",
            descripcion = "Periferia industrial: talleres, almacenes y secciones de Micro 70. Talleres tecnológicos educativos.",
            color = 0xFF795548.toInt(),
            tieneEducacion = true
        ),

        Circuito(
            codigo = "A133", nombre = "Pueblo Nuevo (par)",
            zona = "Nueva Gerona",
            descripcion = "Comparte bloque con A070. Policlínico de Pueblo Nuevo, estación de bombeo de Pueblo Nuevo y Escuela Especial.",
            color = 0xFF673AB7.toInt(),
            tieneSalud = true, tieneBombeo = true, tieneEducacion = true
        ),

        Circuito(
            codigo = "A147", nombre = "La Demajagua",
            zona = "La Demajagua",
            descripcion = "Poblado de La Demajagua (Santa Bárbara). Incluye Policlínico, internado de primaria " +
                "y estaciones de bombeo Oeste (vitales para Nueva Gerona).",
            color = 0xFFE91E63.toInt(),
            tieneSalud = true, tieneBombeo = true, tieneEducacion = true
        ),

        Circuito(
            codigo = "A210", nombre = "Santa Fe (servicios críticos)",
            zona = "Santa Fe",
            descripcion = "Casco histórico de La Fe, reparto Camilo, Cochabamba y Carretera Júcaro hasta el Mesón. " +
                "Alimenta ETECSA Santa Fe, radio base, Hogar Materno y Casa de Abuelos.",
            color = 0xFFFF9800.toInt(),
            tieneSalud = true, tieneTelecom = true
        ),

        Circuito(
            codigo = "A220", nombre = "Periferia / Rural Norte NG",
            zona = "Nueva Gerona",
            descripcion = "Repartos suburbanos de Nueva Gerona, afueras y zonas agrícolas. " +
                "Incluye consultorios periféricos, pozos rurales del norte y la EIDE.",
            color = 0xFF00BCD4.toInt(),
            tieneSalud = true, tieneBombeo = true, tieneEducacion = true
        ),

        Circuito(
            codigo = "A310", nombre = "Santa Fe Centro-Norte",
            zona = "Santa Fe",
            descripcion = "Casco urbano de Santa Fe: Policlínico Orestes Falls, áreas comerciales y escuelas céntricas.",
            color = 0xFFFFC107.toInt(),
            tieneSalud = true, tieneEducacion = true
        ),

        Circuito(
            codigo = "A330", nombre = "Santa Fe – Reparto Camilo",
            zona = "Santa Fe",
            descripcion = "Reparto Camilo Cienfuegos, Cochabamba y ramales residenciales. " +
                "Consultorios, sistema de abasto interno de Santa Fe y Círculo Infantil.",
            color = 0xFFFF5722.toInt(),
            tieneSalud = true, tieneBombeo = true, tieneEducacion = true
        ),

        Circuito(
            codigo = "A340", nombre = "Santa Fe – Hospitales",
            zona = "Santa Fe",
            descripcion = "Eje técnico con prioridad a servicios de salud. Clínicas estomatológicas, " +
                "rehabilitación y puntos de carga de pipas de agua. Se extiende hacia Carretera a Júcaro.",
            color = 0xFF8BC34A.toInt(),
            tieneSalud = true, tieneBombeo = true
        ),

        Circuito(
            codigo = "A480", nombre = "Norte de Gerona",
            zona = "Norte",
            descripcion = "Eje norte: Chacón, Playas, Sierra Caballo y área del polígono. " +
                "Puntos médicos de Sierra Caballo/Chacón y Escuela Formadora de Maestros.",
            color = 0xFF009688.toInt(),
            tieneSalud = true, tieneEducacion = true
        )
    )
}