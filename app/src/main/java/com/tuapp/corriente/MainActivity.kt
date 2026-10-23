package com.tuapp.corriente

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val tv = TextView(this)
        tv.text = "¡LA APP FUNCIONA!\n\nSi ves este mensaje, el problema estaba en la Base de Datos, en Compose o en el Tema."
        tv.textSize = 18f
        tv.setPadding(50, 100, 50, 50)
        
        setContentView(tv)
    }
}