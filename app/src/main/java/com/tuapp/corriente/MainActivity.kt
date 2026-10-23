package com.tuapp.corriente

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val logFile = File(filesDir, "crash_log.txt")

        if (logFile.exists()) {
            // SI HAY UN ERROR GUARDADO, LO MOSTRAMOS EN PANTALLA
            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(40, 40, 40, 40)
            }

            val btnLimpiar = Button(this).apply {
                text = "Borrar registro y reintentar"
                setOnClickListener {
                    logFile.delete()
                    recreate() // Reinicia la activity
                }
            }

            val tvError = TextView(this).apply {
                text = "ERROR DETECTADO:\n\n${logFile.readText()}"
                textSize = 12f
                setTextIsSelectable(true)
            }

            val scroll = ScrollView(this).apply {
                addView(tvError)
            }

            layout.addView(btnLimpiar)
            layout.addView(scroll)
            setContentView(layout)
            
        } else {
            // SI NO HAY ERRORES, INICIA LA APP NORMALMENTE (Prueba de BD)
            val tv = TextView(this)
            tv.text = "Iniciando prueba...\n\n"
            tv.textSize = 16f
            tv.setPadding(50, 50, 50, 50)
            setContentView(tv)

            try {
                val app = application as CorrienteApp
                val db = app.db

                tv.append("Base de datos instanciada.\n")
                tv.append("Probando lectura...\n\n")

                // Hacemos la consulta de forma asíncrona
                kotlinx.coroutines.GlobalScope.launch {
                    try {
                        val bloques = db.bloqueServicioDao().obtenerTodos()
                        val circuitos = db.circuitoDao().obtenerTodos()
                        
                        runOnUiThread {
                            tv.append("¡ÉXITO!\n")
                            tv.append("Circuitos: ${circuitos.size}\n")
                            tv.append("Bloques: ${bloques.size}\n")
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            tv.append("Error en consulta: ${e.message}\n")
                        }
                    }
                }
            } catch (e: Throwable) {
                tv.append("Error al iniciar BD: ${e.message}\n")
            }
        }
    }
}