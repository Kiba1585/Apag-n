package com.tuapp.corriente

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import com.tuapp.corriente.data.DatosIniciales
import com.tuapp.corriente.data.ScheduleManager
import com.tuapp.corriente.util.HorarioCalculator
import java.time.LocalDateTime

class CorrienteWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) { actualizarWidget(context, appWidgetManager, appWidgetId) }
    }

    private fun actualizarWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_layout)
        val scheduleManager = ScheduleManager(context)
        val codigoFavorito = scheduleManager.obtenerCircuitoFavorito() ?: "A060"
        val circuito = DatosIniciales.circuitos.find { it.codigo == codigoFavorito } ?: DatosIniciales.circuitos[0]
        
        val reglas = scheduleManager.cargarReglas() ?: emptyList()
        val regla = reglas.find { it.circuitoCodigo == circuito.codigo }
        
        val estado = HorarioCalculator.calcular(regla, LocalDateTime.now())
        
        val horas = estado.minutosHastaCambio / 60
        val mins = estado.minutosHastaCambio % 60
        val tiempoTexto = if (horas > 0) "${horas}h ${mins}m" else "${mins}m"
        
        val colorEstado = if (estado.tieneCorriente) Color.parseColor("#4CAF50") else Color.parseColor("#F44336")
        val textoEstado = if (estado.tieneCorriente) "🟢 CON LUZ" else "🔴 SIN LUZ"
        val textoTiempo = when {
            estado.minutosHastaCambio == 0L -> "Sin horario"
            estado.tieneCorriente -> "Quedan $tiempoTexto"
            else -> "Vuelve en $tiempoTexto"
        }

        views.setTextViewText(R.id.widget_codigo, "${circuito.codigo} · ${circuito.nombre}")
        views.setTextViewText(R.id.widget_estado, textoEstado)
        views.setTextColor(R.id.widget_estado, colorEstado)
        views.setTextViewText(R.id.widget_tiempo, textoTiempo)

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.widget_estado, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}