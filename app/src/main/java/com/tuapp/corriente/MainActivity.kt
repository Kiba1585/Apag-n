package com.tuapp.corriente

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this)
        tv.text = "¡FUNCIONA!\n\nSi ves esto, el código está perfecto y el problema es del teléfono."
        tv.textSize = 20f
        setContentView(tv)
    }
}