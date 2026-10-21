package com.tuapp.corriente.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "alarmas",
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
data class Alarma(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val zonaId: Long,
    val bloqueId: Long?,            // null = alarma genérica
    val minutosAntes: Int,          // 15, 30, 60
    val tono: String = "default",
    val vibrar: Boolean = true,
    val volumen: Int = 80,          // 0-100
    val activa: Boolean = true
)