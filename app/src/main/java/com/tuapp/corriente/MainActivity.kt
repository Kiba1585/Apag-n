package com.tuapp.corriente

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tuapp.corriente.data.DatosBloques
import com.tuapp.corriente.data.DatosIniciales
import com.tuapp.corriente.util.HorarioCalculator
import java.time.LocalTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    PantallaEnVivo()
                }
            }
        }
    }
}

@Composable
fun PantallaEnVivo() {
    // 1. Cargamos los datos en memoria
    val circuitos = remember { DatosIniciales.circuitos }
    val todosLosBloques = remember { DatosBloques.generarBloques() }
    
    // 2. Agrupamos los bloques por circuito para que sea rápido buscar
    val bloquesPorCircuito = remember { todosLosBloques.groupBy { it.circuitoCodigo } }
    
    // 3. Obtenemos la hora actual (esto se actualizará cada vez que abras la app)
    val ahora = LocalTime.now()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("⚡ Estado en Vivo", style = MaterialTheme.typography.headlineSmall)
        Text("Hora actual: ${ahora.hour.toString().padStart(2, '0')}:${ahora.minute.toString().padStart(2, '0')}", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(16.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(circuitos, key = { it.codigo }) { c ->
                
                // Calculamos el estado de ESTE circuito en ESTE momento
                val bloquesDeEsteCircuito = bloquesPorCircuito[c.codigo] ?: emptyList()
                val estado = HorarioCalculator.calcular(bloquesDeEsteCircuito, ahora)
                
                // Definimos los colores y textos según el estado
                val colorEstado = when {
                    estado.tieneCorriente && estado.minutosHastaCambio <= 30 -> Color(0xFFFFC107) // Amarillo (cambio próximo)
                    estado.tieneCorriente -> Color(0xFF4CAF50) // Verde (Luz)
                    else -> Color(0xFFF44336) // Rojo (Apagón)
                }
                
                val textoEstado = when {
                    estado.tieneCorriente && estado.minutosHastaCambio <= 30 -> "Corte en ${estado.minutosHastaCambio} min"
                    estado.tieneCorriente -> "Con corriente (quedan ${estado.minutosHastaCambio / 60}h ${estado.minutosHastaCambio % 60}m)"
                    else -> "Sin corriente (vuelve en ${estado.minutosHastaCambio / 60}h ${estado.minutosHastaCambio % 60}m)"
                }

                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = colorEstado.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        // El puntito de color del circuito original
                        Box(Modifier.size(14.dp).clip(CircleShape).background(Color(c.color)))
                        Spacer(Modifier.width(12.dp))
                        
                        Column(Modifier.weight(1f)) {
                            Text("${c.codigo} · ${c.nombre}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(c.zona, style = MaterialTheme.typography.labelSmall)
                            
                            Spacer(Modifier.height(4.dp))
                            
                            // Texto del estado (Verde/Rojo/Amarillo)
                            Text(
                                textoEstado,
                                style = MaterialTheme.typography.labelMedium,
                                color = colorEstado,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        // Indicador visual de estado (Círculo grande)
                        Box(Modifier.size(16.dp).clip(CircleShape).background(colorEstado))
                    }
                }
            }
        }
    }
}