package com.tuapp.corriente.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.tuapp.corriente.data.entity.ReglaHorario
import com.tuapp.corriente.receivers.AlarmaReceiver
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId

object AlarmaScheduler {
    fun programarAlarma(context: Context, regla: ReglaHorario?, codigoCircuito: String) {
        if (regla == null) return
        
        val ahora = LocalDateTime.now()
        val estado = HorarioCalculator.calcular(regla, ahora)
        
        // Si tiene corriente ahora, avisamos antes de que se vaya (opcional). 
        // Si no tiene, avisamos antes de que llegue. (Usaremos esta última).
        var minutosParaCambio = estado.minutosHastaCambio
        
        // Si queremos avisar cuando llegue la luz:
        if (estado.tieneCorriente) {
            // Si ya tiene luz, la próxima vez que llegue será en el próximo ciclo
            val cicloTotal = (regla.horasCorriente + regla.horasApagon) * 60
            minutosParaCambio += (regla.horasApagon * 60) // Saltamos el apagón
        }

        val horaAlarma = ahora.plusMinutes(minutosParaCambio).minusMinutes(15)
        val milisegundos = horaAlarma.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmaReceiver::class.java).apply {
            putExtra("circuito", codigoCircuito)
            putExtra("hora_inicio", horaAlarma.plusMinutes(15).toLocalTime().toString())
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context, codigoCircuito.hashCode(), intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(milisegundos, pendingIntent), pendingIntent)
        } catch (e: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, milisegundos, pendingIntent)
        }
    }

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