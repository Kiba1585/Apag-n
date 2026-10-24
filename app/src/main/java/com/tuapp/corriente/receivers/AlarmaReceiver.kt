package com.tuapp.corriente.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.tuapp.corriente.MainActivity

class AlarmaReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val codigo = intent.getStringExtra("circuito") ?: "Circuito"
        val horaInicio = intent.getStringExtra("hora_inicio") ?: ""

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // CAMBIAMOS EL ID DEL CANAL (v2) PARA FORZAR A ANDROID A CREARLO CON SONIDO
        val channelId = "alarma_corriente_v2"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val channel = NotificationChannel(channelId, "Alarmas de Corriente", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Avisos previos a la llegada de la corriente"
                enableVibration(true)
                vibrationPattern = longArrayOf(1000, 1000, 1000, 1000)
                setSound(soundUri, audioAttributes)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Al tocar la notificación, abrir la app
        val intentApp = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intentApp, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_charging)
            .setContentTitle("¡Llegará la corriente pronto!")
            .setContentText("El circuito $codigo tendrá luz a las $horaInicio. (Aviso 15 min antes)")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(codigo.hashCode(), notification)
    }
}