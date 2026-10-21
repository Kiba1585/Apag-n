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
            entity = Zona::class,
            parentColumns = ["id"],
            childColumns = ["zonaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("zonaId")]
)
data class RegistroHistorico(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val zonaId: Long,
    val inicio: Instant,
    val fin: Instant?,
    val tipo: String                // "corte" | "corriente"
)