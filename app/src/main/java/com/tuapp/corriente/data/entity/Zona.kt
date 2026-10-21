package com.tuapp.corriente.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "zonas")
data class Zona(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val color: Int,                    // ARGB
    val activa: Boolean = true,
    val esquema: String = "2x8",       // "2x8", "4x6", ...
    val offsetRotacion: Int = 0        // días de desfase para la rotación
)
