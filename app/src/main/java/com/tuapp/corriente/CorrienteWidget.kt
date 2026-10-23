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
        
        // 1. Obtener el circuito favorito. Si no hay, usamos A060 por defecto.
        val codigoFavorito = scheduleManager.obtenerCircuitoFavorito() ?: "A060"
        val circuito = DatosIniciales.circuitos.find { it.codigo == codigoFavorito } ?: DatosIniciales.circuitos[0]
        
        // 2. Obtener los bloques (de SharedPreferences o los de fábrica)
        val todosLosBloques = scheduleManager.cargarBloques() ?: DatosBloques.generarBloques()
        val bloquesDelCircuito = todosLosBloques.filter { it.circuitoCodigo == circuito.codigo }
        
        // 3. Calcular el estado actual
        val ahora = LocalTime.now()
        val estado = HorarioCalculator.calcular(bloquesDelCircuito, ahora)
        
        // 4. Formatear textos
        val horas = estado.minutosHastaCambio / 60
        val mins = estado.minutosHastaCambio % 60
        val tiempoTexto = if (horas > 0) "${horas}h ${mins}m" else "${mins}m"
        
        val colorFondo = if (estado.tieneCorriente) "#2E7D32" else "#C62828" // Verde o Rojo oscuro
        val textoEstado = if (estado.tieneCorriente) "CON LUZ" else "SIN LUZ"
        val textoTiempo = if (estado.tieneCorriente) "Quedan $tiempoTexto" else "Vuelve en $tiempoTexto"

        // 5. Asignar los valores a la vista
        views.setTextViewText(R.id.widget_codigo, "${circuito.codigo} · ${circuito.nombre}")
        views.setTextViewText(R.id.widget_estado, textoEstado)
        views.setTextViewText(R.id.widget_tiempo, textoTiempo)
        views.setInt(R.id.widget_estado, "setBackgroundColor", android.graphics.Color.parseColor(colorFondo))
        views.setTextColor(R.id.widget_estado, android.graphics.Color.WHITE)

        // 6. Al tocar el widget, abrir la app
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.widget_codigo, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}