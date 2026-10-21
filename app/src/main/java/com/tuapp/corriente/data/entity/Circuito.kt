package com.tuapp.corriente.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "circuitos")
data class Circuito(
    @PrimaryKey val codigo: String,       // "A060"
    val nombre: String,                    // "Nueva Gerona Centro"
    val zona: String,                      // "Nueva Gerona", "Santa Fe", ...
    val descripcion: String,               // límites de calles
    val color: Int,                        // ARGB
    val activo: Boolean = true
)