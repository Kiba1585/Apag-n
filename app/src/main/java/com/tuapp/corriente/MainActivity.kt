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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tuapp.corriente.data.DatosBloques
import com.tuapp.corriente.data.DatosIniciales
import com.tuapp.corriente.util.HorarioCalculator
import kotlinx.coroutines.delay
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
    val circuitos = remember { DatosIniciales.circuitos }
    val todosLosBloques = remember { DatosBloques.generarBloques() }
    val bloquesPorCircuito = remember { todosLosBloques.groupBy { it.circuitoCodigo } }

    // Estado para guardar la hora actual y actualizarla cada minuto
    var ahora by remember { mutableStateOf(LocalTime.now()) }

    // Este efecto se ejecuta al abrir la pantalla y actualiza la hora cada 60 segundos
    LaunchedEffect(Unit) {
        while (true) {
            ahora = LocalTime.now()
            delay(60_000L) // 60 segundos
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⚡ Estado en Vivo", style = MaterialTheme.typography.headlineSmall)
            Text(
                "${ahora.hour.toString().padStart(2, '0')}:${ahora.minute.toString().padStart(2, '0')}", 
                style = MaterialTheme.typography.titleLarge, 
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(16.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(circuitos, key = { it.codigo }) { c ->
                val bloquesDeEsteCircuito = bloquesPorCircuito[c.codigo] ?: emptyList()
                val estado = HorarioCalculator.calcular(bloquesDeEsteCircuito, ahora)
                
                val colorEstado = when {
                    estado.tieneCorriente && estado.minutosHastaCambio <= 30 -> Color(0xFFFFC107) // Amarillo
                    estado.tieneCorriente -> Color(0xFF4CAF50) // Verde
                    else -> Color(0xFFF44336) // Rojo
                }
                
                // Formatear el tiempo restante
                val horas = estado.minutosHastaCambio / 60
                val mins = estado.minutosHastaCambio % 60
                val tiempoTexto = if (horas > 0) "${horas}h ${mins}m" else "${mins}m"

                val textoEstado = when {
                    estado.tieneCorriente && estado.minutosHastaCambio <= 30 -> "Corte en $tiempoTexto"
                    estado.tieneCorriente -> "Con corriente (quedan $tiempoTexto)"
                    else -> "Sin corriente (vuelve en $tiempoTexto)"
                }

                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = colorEstado.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(14.dp).clip(CircleShape).background(Color(c.color)))
                        Spacer(Modifier.width(12.dp))
                        
                        Column(Modifier.weight(1f)) {
                            Text("${c.codigo} · ${c.nombre}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(c.zona, style = MaterialTheme.typography.labelSmall)
                            
                            Spacer(Modifier.height(4.dp))
                            
                            Text(
                                textoEstado,
                                style = MaterialTheme.typography.labelMedium,
                                color = colorEstado,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Box(Modifier.size(16.dp).clip(CircleShape).background(colorEstado))
                    }
                }
            }
        }
    }
}