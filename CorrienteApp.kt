package com.tuapp.corriente

import android.app.Application
import com.tuapp.corriente.data.AppDatabase

class CorrienteApp : Application() {
    val db: AppDatabase by lazy { AppDatabase.obtener(this) }
}