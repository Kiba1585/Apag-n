package com.tuapp.corriente

import android.app.Application
import com.tuapp.corriente.data.AppDatabase
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class CorrienteApp : Application() {
    val db: AppDatabase by lazy { AppDatabase.obtener(this) }

    override fun onCreate() {
        super.onCreate()
        
        // Manejador global de errores
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val sw = StringWriter()
                throwable.printStackTrace(PrintWriter(sw))
                val logFile = File(filesDir, "crash_log.txt")
                logFile.writeText(sw.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            // Dejar que el sistema cierre la app normalmente
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}