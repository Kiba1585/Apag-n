package com.tuapp.corriente.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(
    tableName = "bloques_horario",
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
data class BloqueHorario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val zonaId: Long,
    val diaSemana: Int,             // 1 = lunes ... 7 = domingo
    val horaInicio: LocalTime,
    val horaFin: LocalTime,
    val tieneCorriente: Boolean     // true = luz, false = apagón
)