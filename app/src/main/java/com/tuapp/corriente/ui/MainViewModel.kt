package com.tuapp.corriente.ui

import androidx.lifecycle.ViewModel
import com.tuapp.corriente.data.DatosIniciales
import com.tuapp.corriente.data.entity.Circuito
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    // Usamos una lista en memoria en lugar de Room
    private val _circuitos = MutableStateFlow<List<Circuito>>(emptyList())
    val circuitos: StateFlow<List<Circuito>> = _circuitos.asStateFlow()

    init {
        // Cargamos los circuitos iniciales
        _circuitos.value = DatosIniciales.circuitos
    }
}