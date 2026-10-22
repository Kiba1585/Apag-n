package com.tuapp.corriente.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "alarmas",
    foreignKeys = [
        ForeignKey(
            entity = Circuito::class,
            parentColumns = ["codigo"],
            childColumns = ["circuitoCodigo"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("circuitoCodigo")]
)
data class Alarma(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val circuitoCodigo: String,
    val bloqueId: Long?,            // null = alarma genérica
    val minutosAntes: Int,          // 15, 30, 60
    val tono: String = "default",
    val vibrar: Boolean = true,
    val volumen: Int = 80,          // 0-100
    val activa: Boolean = true
)