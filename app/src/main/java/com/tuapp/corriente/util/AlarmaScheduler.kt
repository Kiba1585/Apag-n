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
        
        var proximoBloque: BloqueServicio? = null
        var inicioBloque: LocalDateTime? = null

        val bloquesOrdenados = bloques.sortedBy { it.horaInicio }

        for (bloque in bloquesOrdenados) {
            if (bloque.horaInicio.isAfter(horaActual)) {
                proximoBloque = bloque
                inicioBloque = ahora.toLocalDate().atTime(bloque.horaInicio)
                break
            } else if (bloque.horaInicio.isBefore(horaActual) && bloque.horaFin.isBefore(horaActual)) {
                proximoBloque = bloque
                inicioBloque = ahora.toLocalDate().plusDays(1).atTime(bloque.horaInicio)
                break
            }
        }

        if (proximoBloque != null && inicioBloque != null) {
            val horaAlarma = inicioBloque.minusMinutes(15)
            val horaAlarmaFinal = if (horaAlarma.isBefore(ahora)) horaAlarma.plusDays(1) else horaAlarma
            
            val milisegundos = horaAlarmaFinal.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmaReceiver::class.java).apply {
                putExtra("circuito", codigoCircuito)
                putExtra("hora_inicio", proximoBloque.horaInicio.toString())
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context, codigoCircuito.hashCode(), intent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                alarmManager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(milisegundos, pendingIntent),
                    pendingIntent
                )
            } catch (e: SecurityException) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, milisegundos, pendingIntent)
            }
        }
    }

    // FUNCIÓN PARA CANCELAR LA ALARMA
    fun cancelarAlarma(context: Context, codigoCircuito: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmaReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, codigoCircuito.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}