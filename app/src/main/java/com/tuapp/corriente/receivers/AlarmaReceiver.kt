package com.tuapp.corriente.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.tuapp.corriente.R

class AlarmaReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val codigo = intent.getStringExtra("circuito") ?: "Circuito"
        val horaInicio = intent.getStringExtra("hora_inicio") ?: ""

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "alarma_corriente"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Alarmas de Corriente", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_charging) // Icono nativo
            .setContentTitle("¡Llegará la corriente pronto!")
            .setContentText("El circuito $codigo tendrá luz a las $horaInicio. (Aviso 15 min antes)")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .build()

        notificationManager.notify(codigo.hashCode(), notification)
    }
}