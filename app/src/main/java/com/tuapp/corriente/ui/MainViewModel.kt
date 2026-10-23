package com.tuapp.corriente.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tuapp.corriente.CorrienteApp
import com.tuapp.corriente.data.DatosBloques
import com.tuapp.corriente.data.DatosIniciales
import com.tuapp.corriente.data.entity.Circuito
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {
    
    // Usamos 'as?' para no crashear si el manifest está mal
    private val appReal = app as? CorrienteApp 
        ?: error("ERROR: El AndroidManifest.xml no tiene android:name=\".CorrienteApp\"")
        
    private val db = appReal.db
    private val circuitoDao = db.circuitoDao()
    private val bloqueDao = db.bloqueServicioDao()

    val circuitos = circuitoDao.observarTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            try {
                if (circuitoDao.contar() == 0) {
                    circuitoDao.insertarVarios(DatosIniciales.circuitos)
                }
                if (bloqueDao.obtenerTodos().isEmpty()) {
                    bloqueDao.insertarVarios(DatosBloques.generarBloques())
                }
            } catch (e: Exception) {
                e.printStackTrace()
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