package com.tuapp.corriente.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "historial",
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
data class RegistroHistorico(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val circuitoCodigo: String,
    val inicio: Instant,
    val fin: Instant?,
    val tipo: String                // "corte" | "corriente"
)