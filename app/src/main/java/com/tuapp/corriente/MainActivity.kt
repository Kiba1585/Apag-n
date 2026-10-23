package com.tuapp.corriente

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val tv = TextView(this)
        tv.text = "Iniciando prueba de base de datos...\n\n"
        tv.textSize = 16f
        tv.setPadding(50, 50, 50, 50)
        setContentView(tv)

        try {
            val app = application as CorrienteApp
            val db = app.db
            
            lifecycleScope.launch {
                try {
                    val bloques = db.bloqueServicioDao().obtenerTodos()
                    val circuitos = db.circuitoDao().obtenerTodos()
                    
                    tv.append("¡ÉXITO!\n\n")
                    tv.append("Circuitos en BD: ${circuitos.size}\n")
                    tv.append("Bloques en BD: ${bloques.size}\n\n")
                    tv.append("La base de datos funciona correctamente.\n")
                    tv.append("Si ves este mensaje, el problema está en Compose o en el ViewModel.")
                } catch (e: Exception) {
                    tv.append("ERROR EN ROOM:\n")
                    tv.append(e.message + "\n\n")
                    tv.append(e.stackTraceToString())
                }
            }
        } catch (e: Throwable) {
            tv.append("ERROR GENERAL:\n")
            tv.append(e.message + "\n\n")
            tv.append(e.stackTraceToString())
        }
    }
}