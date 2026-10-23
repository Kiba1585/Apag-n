package com.tuapp.corriente.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.tuapp.corriente.data.entity.BloqueServicio
import com.tuapp.corriente.receivers.AlarmaReceiver
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

object AlarmaScheduler {
    fun programarAlarma(context: Context, bloques: List<BloqueServicio>, codigoCircuito: String) {
        val ahora = LocalDateTime.now()
        val horaActual = ahora.toLocalTime()
        
        // Buscar el próximo bloque en el que habrá corriente
        var proximoBloque: BloqueServicio? = null
        var inicioBloque: LocalDateTime? = null

        // Ordenar bloques por hora de inicio
        val bloquesOrdenados = bloques.sortedBy { it.horaInicio }

        for (bloque in bloquesOrdenados) {
            if (bloque.horaInicio.isAfter(horaActual)) {
                // Es hoy
                proximoBloque = bloque
                inicioBloque = ahora.toLocalDate().atTime(bloque.horaInicio)
                break
            } else if (bloque.horaInicio.isBefore(horaActual) && bloque.horaFin.isBefore(horaActual)) {
                // Ya pasó, el próximo será mañana
                proximoBloque = bloque
                inicioBloque = ahora.toLocalDate().plusDays(1).atTime(bloque.horaInicio)
                break
            }
        }

        if (proximoBloque != null && inicioBloque != null) {
            // Restamos 15 minutos para avisar antes
            val horaAlarma = inicioBloque.minusMinutes(15)
            
            // Si la hora de la alarma ya pasó (ej. son las 1:55 y el bloque empieza a las 2:00),
            // la programamos para el día siguiente.
            val horaAlarmaFinal = if (horaAlarma.isBefore(ahora)) horaAlarma.plusDays(1) else horaAlarma
            
            val milisegundos = horaAlarmaFinal.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmaReceiver::class.java).apply {
                putExtra("circuito", codigoCircuito)
                putExtra("hora_inicio", proximoBloque.horaInicio.toString())
            }
            
            // Usamos hashCode para que cada circuito tenga su propia alarma independiente
            val pendingIntent = PendingIntent.getBroadcast(
                context, codigoCircuito.hashCode(), intent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Programar alarma exacta (despierta el teléfono)
            try {
                alarmManager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(milisegundos, pendingIntent),
                    pendingIntent
                )
            } catch (e: SecurityException) {
                // Si falta el permiso en versiones nuevas de Android
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, milisegundos, pendingIntent)
            }
        }
    }
}