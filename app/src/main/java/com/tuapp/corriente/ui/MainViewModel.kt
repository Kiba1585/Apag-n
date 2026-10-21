package com.tuapp.corriente.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tuapp.corriente.CorrienteApp
import com.tuapp.corriente.data.DatosIniciales
import com.tuapp.corriente.data.entity.BloqueServicio
import com.tuapp.corriente.data.entity.Circuito
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val db = (app as CorrienteApp).db
    private val circuitoDao = db.circuitoDao()
    private val bloqueDao = db.bloqueServicioDao()

    val circuitos = circuitoDao.observarTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            if (circuitoDao.contar() == 0) {
                circuitoDao.insertarVarios(DatosIniciales.circuitos)
            }
        }
    }

    fun eliminarCircuito(circuito: Circuito) {
        viewModelScope.launch { circuitoDao.eliminar(circuito) }
    }

    fun agregarCircuito(circuito: Circuito) {
        viewModelScope.launch { circuitoDao.insertar(circuito) }
    }
}