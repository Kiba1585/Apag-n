package com.tuapp.corriente

import android.Manifest
import android.app.TimePickerDialog
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.core.content.ContextCompat
import com.tuapp.corriente.data.DatosBloques
import com.tuapp.corriente.data.DatosIniciales
import com.tuapp.corriente.data.ScheduleManager
import com.tuapp.corriente.data.entity.BloqueServicio
import com.tuapp.corriente.data.entity.Circuito
import com.tuapp.corriente.util.AlarmaScheduler
import com.tuapp.corriente.util.HorarioCalculator
import kotlinx.coroutines.delay
import java.time.LocalTime
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(this, "Sin permiso de notificaciones, las alarmas no sonarán", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    PantallaEnVivo()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEnVivo() {
    val context = LocalContext.current
    val scheduleManager = remember { ScheduleManager(context) }
    
    var circuitos by remember { mutableStateOf(scheduleManager.cargarCircuitos() ?: DatosIniciales.circuitos) }
    var todosLosBloques by remember { mutableStateOf(scheduleManager.cargarBloques() ?: DatosBloques.generarBloques()) }
    var favorito by remember { mutableStateOf(scheduleManager.obtenerCircuitoFavorito() ?: "A060") }
    
    // Estado para saber qué alarmas están activas
    var alarmasActivas by remember { mutableStateOf(scheduleManager.obtenerAlarmasActivas()) }
    
    val bloquesPorCircuito = remember(todosLosBloques) { todosLosBloques.groupBy { it.circuitoCodigo } }
    var ahora by remember { mutableStateOf(LocalTime.now()) }
    var circuitoAEditar by remember { mutableStateOf<Circuito?>(null) }
    var mostrarDialogoAgregar by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            ahora = LocalTime.now()
            delay(60_000L)
        }
    }

    val circuitosOrdenados = circuitos.map { c ->
        val bloques = bloquesPorCircuito[c.codigo] ?: emptyList()
        val estado = HorarioCalculator.calcular(bloques, ahora)
        Pair(c, estado)
    }.sortedWith(
        compareByDescending<Pair<Circuito, HorarioCalculator.Estado>> { it.second.tieneCorriente }
            .thenBy { it.second.minutosHastaCambio }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("⚡ Estado en Vivo") },
                actions = {
                    Text(
                        "${ahora.hour.toString().padStart(2, '0')}:${ahora.minute.toString().padStart(2, '0')}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogoAgregar = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Circuito")
            }
        }
    ) { padding ->
        if (circuitosOrdenados.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No tienes circuitos. Toca + para añadir uno.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
            ) {
                items(circuitosOrdenados, key = { it.first.codigo }) { (c, estado) ->
                    
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

                    // Comprobamos si ESTE circuito tiene la alarma activa
                    val alarmaActivada = alarmasActivas.contains(c.codigo)

                    Card(
                        Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = colorEstado.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(14.dp).clip(CircleShape).background(Color(c.color)))
                            Spacer(Modifier.width(8.dp))
                            Column(Modifier.weight(1f)) {
                                Text("${c.codigo} · ${c.nombre}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(textoEstado, style = MaterialTheme.typography.labelMedium, color = colorEstado, fontWeight = FontWeight.Bold)
                            }
                            
                            // BOTÓN DE LA CAMPANA
                            IconButton(onClick = {
                                if (alarmaActivada) {
                                    // Desactivar
                                    AlarmaScheduler.cancelarAlarma(context, c.codigo)
                                    scheduleManager.desactivarAlarma(c.codigo)
                                    alarmasActivas = alarmasActivas - c.codigo
                                    Toast.makeText(context, "Alarma desactivada", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Activar
                                    AlarmaScheduler.programarAlarma(context, bloquesPorCircuito[c.codigo] ?: emptyList(), c.codigo)
                                    scheduleManager.activarAlarma(c.codigo)
                                    alarmasActivas = alarmasActivas + c.codigo
                                    Toast.makeText(context, "Alarma activada (15 min antes)", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Alarma",
                                    // Azul si está activa, gris si no
                                    tint = if (alarmaActivada) Color(0xFF2196F3) else Color.Gray
                                )
                            }

                            IconButton(onClick = {
                                favorito = c.codigo
                                scheduleManager.guardarCircuitoFavorito(c.codigo)
                                val intent = Intent(context, CorrienteWidget::class.java).apply {
                                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                                    val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, CorrienteWidget::class.java))
                                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                                }
                                context.sendBroadcast(intent)
                            }) {
                                Icon(Icons.Default.Star, contentDescription = "Favorito", tint = if (favorito == c.codigo) Color(0xFFFFC107) else Color.Gray)
                            }

                            IconButton(onClick = { circuitoAEditar = c }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                            }
                            
                            IconButton(onClick = {
                                val nuevaLista = circuitos.filter { it.codigo != c.codigo }
                                circuitos = nuevaLista
                                scheduleManager.guardarCircuitos(nuevaLista)
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogoAgregar) {
        AgregarCircuitoDialog(
            onDismiss = { mostrarDialogoAgregar = false },
            onAdd = { nuevo ->
                val nuevaLista = circuitos + nuevo
                circuitos = nuevaLista
                scheduleManager.guardarCircuitos(nuevaLista)
                mostrarDialogoAgregar = false
            }
        )
    }

    if (circuitoAEditar != null) {
        EditarHorarioDialog(
            circuito = circuitoAEditar!!,
            bloquesActuales = bloquesPorCircuito[circuitoAEditar!!.codigo] ?: emptyList(),
            onDismiss = { circuitoAEditar = null },
            onSave = { nuevos: List<BloqueServicio> ->
                val otrosCircuitos = todosLosBloques.filter { it.circuitoCodigo != circuitoAEditar!!.codigo }
                val listaActualizada = otrosCircuitos + nuevos
                todosLosBloques = listaActualizada
                scheduleManager.guardarBloques(listaActualizada)
                circuitoAEditar = null
            }
        )
    }
}

@Composable
fun AgregarCircuitoDialog(
    onDismiss: () -> Unit,
    onAdd: (Circuito) -> Unit
) {
    var codigo by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir Circuito") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = codigo,
                    onValueChange = { codigo = it },
                    label = { Text("Código (ej. A999)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre (ej. Mi Casa)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = codigo.isNotBlank() && nombre.isNotBlank(),
                onClick = {
                    val color = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
                    onAdd(
                        Circuito(
                            codigo = codigo.trim().uppercase(),
                            nombre = nombre.trim(),
                            zona = "Personalizado",
                            descripcion = "Añadido por el usuario",
                            color = color
                        )
                    )
                }
            ) { Text("Añadir") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

// (El resto de diálogos EditarHorarioDialog y GeneradorPlantillaDialog se mantienen igual)
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
                while (true) {
                    val fin = inicioActual.plusHours(duracion)
                    nuevosBloques.add(
                        BloqueServicio(
                            id = System.currentTimeMillis() + nuevosBloques.size,
     