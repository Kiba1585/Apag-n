package com.tuapp.corriente.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tuapp.corriente.CorrienteApp
import com.tuapp.corriente.data.entity.Zona
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val db = (app as CorrienteApp).db
    private val zonaDao = db.zonaDao()

    val zonas = zonaDao.observarTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun agregarZona(nombre: String, color: Int = 0xFFFFC107.toInt()) {
        viewModelScope.launch {
            zonaDao.insertar(Zona(nombre = nombre, color = color))
        }
    }

    fun eliminarZona(zona: Zona) {
        viewModelScope.launch { zonaDao.eliminar(zona) }
    }
}