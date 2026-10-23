package com.tuapp.corriente

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.tuapp.corriente.data.DatosBloques
import com.tuapp.corriente.data.DatosIniciales
import com.tuapp.corriente.data.ScheduleManager
import com.tuapp.corriente.util.HorarioCalculator
import java.time.LocalTime

class CorrienteWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            actualizarWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun actualizarWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_layout)
        val scheduleManager = ScheduleManager(context)
        
        val codigoFavorito = scheduleManager.obtenerCircuitoFavorito() ?: "A060"
        val circuito = DatosIniciales.circuitos.find { it.codigo == codigoFavorito } ?: DatosIniciales.circuitos[0]
        
        val todosLosBloques = scheduleManager.cargarBloques() ?: DatosBloques.generarBloques()
        val bloquesDelCircuito = todosLosBloques.filter { it.circuitoCodigo == circuito.codigo }
        
        val ahora = LocalTime.now()
        val estado = HorarioCalculator.calcular(bloquesDelCircuito, ahora)
        
        val horas = estado.minutosHastaCambio / 60
        val mins = estado.minutosHastaCambio % 60
        val tiempoTexto = if (horas > 0) "${horas}h ${mins}m" else "${mins}m"
        
        val emoji = if (estado.tieneCorriente) "🟢" else "🔴"

        views.setTextViewText(R.id.widget_estado, "${circuito.codigo} $emoji")
        views.setTextViewText(R.id.widget_tiempo, tiempoTexto)

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.widget_estado, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}