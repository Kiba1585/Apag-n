package com.tuapp.corriente.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(
    tableName = "bloques_servicio",
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
data class BloqueServicio(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val circuitoCodigo: String,
    val horaInicio: LocalTime,
    val horaFin: LocalTime
)