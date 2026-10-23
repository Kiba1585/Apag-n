package com.tuapp.corriente

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
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
    
    var todosLosBloques by remember { 
        mutableStateOf(scheduleManager.cargarBloques() ?: DatosBloques.generarBloques()) 
    }
    
    val bloquesPorCircuito = remember(todosLosBloques) { todosLosBloques.groupBy { it.circuitoCodigo } }
    var ahora by remember { mutableStateOf(LocalTime.now()) }
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

    if (circuitoAEditar != null) {
        EditarHorarioDialog(
            circuito = circuitoAEditar!!,
            bloquesActuales = bloquesPorCircuito[circuitoAEditar!!.codigo] ?: emptyList(),
            onDismiss = { circuitoAEditar = null },
            onSave = { nuevosBloques ->
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
    var listaEditada by remember { mutableStateOf(bloquesActuales.toList()) }
    var mostrarGenerador by remember { mutableStateOf(false) }

    if (mostrarGenerador) {
        GeneradorPlantillaDialog(
            circuito = circuito,
            onDismiss = { mostrarGenerador = false },
            onGenerar = { nuevos ->
                listaEditada = nuevos
                mostrarGenerador = false
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Horario: ${circuito.codigo}") },
        text = {
            Column {
                if (listaEditada.isNotEmpty()) {
                    TextButton(onClick = { listaEditada = emptyList() }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) {
                        Text("Vaciar todos los bloques")
                    }
                } else {
                    Text("No hay bloques asignados.", style = MaterialTheme.typography.bodySmall)
                }

                LazyColumn(Modifier.heightIn(max = 250.dp)) {
                    items(listaEditada.sortedBy { it.horaInicio }) { bloque ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("${bloque.horaInicio} a ${bloque.horaFin}", style = MaterialTheme.typography.bodyMedium)
                            IconButton(onClick = { listaEditada = listaEditada - bloque }) {
                                Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = Color.Red)
                            }
                        }
                    }
                }
                
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { mostrarGenerador = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Plantilla rápida")
                    }
                    
                    Button(
                        onClick = {
                            TimePickerDialog(context, { _, hIni, mIni ->
                                val inicio = LocalTime.of(hIni, mIni)
                                TimePickerDialog(context, { _, hFin, mFin ->
                                    val fin = LocalTime.of(hFin, mFin)
                                    val nuevo = BloqueServicio(
                                        id = System.currentTimeMillis(),
                                        circuitoCodigo = circuito.codigo,
                                        horaInicio = inicio,
                                        horaFin = fin
                                    )
                                    listaEditada = listaEditada + nuevo
                                }, inicio.plusHours(3).hour, inicio.plusHours(3).minute, true).show()
                            }, 12, 0, true).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Manual")
                    }
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

@Composable
fun GeneradorPlantillaDialog(
    circuito: Circuito,
    onDismiss: () -> Unit,
    onGenerar: (List<BloqueServicio>) -> Unit
) {
    val context = LocalContext.current
    var horasCorriente by remember { mutableStateOf("2") }
    var horasApagon by remember { mutableStateOf("8") }
    var horaInicio by remember { mutableStateOf(LocalTime.of(0, 0)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Plantilla Automática") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Configura el nuevo esquema (ej. 2x8):", style = MaterialTheme.typography.bodySmall)
                
                OutlinedTextField(
                    value = horasCorriente,
                    onValueChange = { horasCorriente = it },
                    label = { Text("Horas de corriente (ej. 2)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = horasApagon,
                    onValueChange = { horasApagon = it },
                    label = { Text("Horas de apagón (ej. 8)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                
                Button(onClick = {
                    TimePickerDialog(context, { _, h, m ->
                        horaInicio = LocalTime.of(h, m)
                    }, horaInicio.hour, horaInicio.minute, true).show()
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Inicio del 1er bloque: $horaInicio")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val duracion = horasCorriente.toLongOrNull() ?: 2L
                val apagon = horasApagon.toLongOrNull() ?: 8L
                val nuevosBloques = mutableListOf<BloqueServicio>()
                
                var inicioActual = horaInicio
                // Generamos bloques hasta que el inicio pase de las 23:59
                while (true) {
                    val fin = inicioActual.plusHours(duracion)
                    nuevosBloques.add(
                        BloqueServicio(
                            id = System.currentTimeMillis() + nuevosBloques.size,
                            circuitoCodigo = circuito.codigo,
                            horaInicio = inicioActual,
                            horaFin = fin
                        )
                    )
                    
                    // El siguiente inicio es el fin actual + las horas de apagón
                    inicioActual = fin.plusHours(apagon)
                    
                    // Si el siguiente bloque empieza al día siguiente o el mismo día pero más temprano, paramos
                    if (inicioActual.isBefore(horaInicio) || inicioActual == horaInicio) break
                    if (nuevosBloques.size > 10) break // Seguro para no hacer bucle infinito
                }
                
                onGenerar(nuevosBloques)
            }) { Text("Generar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}