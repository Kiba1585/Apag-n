package com.tuapp.corriente.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.tuapp.corriente.CorrienteWidget
import com.tuapp.corriente.data.entity.ReglaHorario
import java.time.LocalDateTime
import java.time.ZoneId

object WidgetUpdateScheduler {
    fun programarSiguienteActualizacion(context: Context, regla: ReglaHorario?) {
        if (regla == null) return
        
        val ahora = LocalDateTime.now()
        val estado = HorarioCalculator.calcular(regla, ahora)
        
        // El widget se actualizará exactamente cuando el estado cambie (luz a apagón o viceversa)
        val proximoCambio = ahora.plusMinutes(estado.minutosHastaCambio)
        // Aseguramos que no sea en el mismo minuto para evitar bucles
        val horaAlarma = if (proximoCambio.minute == ahora.minute) ahora.plusMinutes(1) else proximoCambio

        val milisegundos = horaAlarma.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, CorrienteWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, CorrienteWidget::class.java))
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context, 9999, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            // setExactAndAllowWhileIdle despierta el teléfono en el minuto exacto
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, milisegundos, pendingIntent)
        } catch (e: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, milisegundos, pendingIntent)
        }
    }
}