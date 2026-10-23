package com.tuapp.corriente

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tuapp.corriente.data.DatosBloques
import com.tuapp.corriente.data.DatosIniciales
import com.tuapp.corriente.data.ScheduleManager
import com.tuapp.corriente.data.entity.BloqueServicio
import com.tuapp.corriente.data.entity.Circuito
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
    val context = LocalContext.current
    val scheduleManager = remember { ScheduleManager(context) }
    val circuitos = remember { DatosIniciales.circuitos }
    
    // Cargamos los bloques desde SharedPreferences. Si no hay, usamos los de fábrica.
    var todosLosBloques by remember { 
        mutableStateOf(scheduleManager.cargarBloques() ?: DatosBloques.generarBloques()) 
    }
    
    val bloquesPorCircuito = remember(todosLosBloques) { todosLosBloques.groupBy { it.circuitoCodigo } }
    var ahora by remember { mutableStateOf(LocalTime.now()) }
    
    // Para el diálogo de edición
    var circuitoAEditar by remember { mutableStateOf<Circuito?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            ahora = LocalTime.now()
            delay(60_000L)
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("⚡ Estado en Vivo", style = MaterialTheme.typography.headlineSmall)
            Text("${ahora.hour.toString().padStart(2, '0')}:${ahora.minute.toString().padStart(2, '0')}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(16.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(circuitos, key = { it.codigo }) { c ->
                val bloquesDeEsteCircuito = bloquesPorCircuito[c.codigo] ?: emptyList()
                val estado = HorarioCalculator.calcular(bloquesDeEsteCircuito, ahora)
                
                val colorEstado = when {
                    estado.tieneCorriente && estado.minutosHastaCambio <= 30 -> Color(0xFFFFC107)
                    estado.tieneCorriente -> Color(0xFF4CAF50)
                    else -> Color(0xFFF44336)
                }
                
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
                            Text(textoEstado, style = MaterialTheme.typography.labelMedium, color = colorEstado, fontWeight = FontWeight.Bold)
                        }
                        
                        IconButton(onClick = { circuitoAEditar = c }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar horario", tint = MaterialTheme.colorScheme.primary)
                        }
                        
                        Box(Modifier.size(16.dp).clip(CircleShape).background(colorEstado))
                    }
                }
            }
        }
    }

    // DIÁLOGO DE EDICIÓN DE HORARIOS
    if (circuitoAEditar != null) {
        EditarHorarioDialog(
            circuito = circuitoAEditar!!,
            bloquesActuales = bloquesPorCircuito[circuitoAEditar!!.codigo] ?: emptyList(),
            onDismiss = { circuitoAEditar = null },
            onSave = { nuevosBloques ->
                // Filtramos los bloques viejos de este circuito y agregamos los nuevos
                val otrosCircuitos = todosLosBloques.filter { it.circuitoCodigo != circuitoAEditar!!.codigo }
                val listaActualizada = otrosCircuitos + nuevosBloques
                todosLosBloques = listaActualizada
                scheduleManager.guardarBloques(listaActualizada)
                circuitoAEditar = null
            }
        )
    }
}

@Composable
fun EditarHorarioDialog(
    circuito: Circuito,
    bloquesActuales: List<BloqueServicio>,
    onDismiss: () -> Unit,
    onSave: (List<BloqueServicio>) -> Unit
) {
    val context = LocalContext.current
    // Copia local para ir editando
    var listaEditada by remember { mutableStateOf(bloquesActuales.toList()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Horario: ${circuito.codigo}") },
        text = {
            Column {
                if (listaEditada.isEmpty()) {
                    Text("No hay bloques. Añade uno abajo.", style = MaterialTheme.typography.bodySmall)
                }
                LazyColumn(Modifier.heightIn(max = 300.dp)) {
                    items(listaEditada) { bloque ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("${bloque.horaInicio} a ${bloque.horaFin}", style = MaterialTheme.typography.bodyMedium)
                            IconButton(onClick = { listaEditada = listaEditada - bloque }) {
                                Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = Color.Red)
                            }
                        }
                    }
                }
                
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        // Al añadir un bloque, pedimos la hora de inicio con el reloj de Android
                        val ahora = LocalTime.now()
                        TimePickerDialog(context, { _, h, m ->
                            val inicio = LocalTime.of(h, m)
                            val fin = inicio.plusHours(3) // Por defecto 3 horas
                            val nuevo = BloqueServicio(
                                id = System.currentTimeMillis(),
                                circuitoCodigo = circuito.codigo,
                                horaInicio = inicio,
                                horaFin = fin
                            )
                            listaEditada = listaEditada + nuevo
                        }, ahora.hour, ahora.minute, true).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Añadir bloque de corriente")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(listaEditada) }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}