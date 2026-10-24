package com.tuapp.corriente

import android.Manifest
import android.app.DatePickerDialog
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
import androidx.compose.foundation.clickable
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
    
    var necesitaOnboarding by remember { mutableStateOf(!scheduleManager.onboardingCompletado()) }

    var circuitos by remember { mutableStateOf(scheduleManager.cargarCircuitos() ?: DatosIniciales.circuitos) }
    var todosLosBloques by remember { mutableStateOf(scheduleManager.cargarBloques() ?: emptyList()) }
    var favorito by remember { mutableStateOf(scheduleManager.obtenerCircuitoFavorito() ?: "A060") }
    var alarmasActivas by remember { mutableStateOf(scheduleManager.obtenerAlarmasActivas()) }
    
    val bloquesPorCircuito = remember(todosLosBloques) { todosLosBloques.groupBy { it.circuitoCodigo } }
    var ahora by remember { mutableStateOf(LocalDateTime.now()) }
    var circuitoAEditar by remember { mutableStateOf<Circuito?>(null) }
    var mostrarDialogoAgregar by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            ahora = LocalDateTime.now()
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

    if (necesitaOnboarding) {
        OnboardingScreen(
            onCircuitoSeleccionado = { circuitoElegido ->
                val nuevaLista = listOf(circuitoElegido)
                circuitos = nuevaLista
                scheduleManager.guardarCircuitos(nuevaLista)
                scheduleManager.marcarOnboardingCompletado()
                necesitaOnboarding = false
            }
        )
        return
    }

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

                    val alarmaActivada = alarmasActivas.contains(c.codigo)

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.StartToEnd) {
                                circuitoAEditar = c 
                            } else if (it == SwipeToDismissBoxValue.EndToStart) {
                                val nuevaLista = circuitos.filter { circ -> circ.codigo != c.codigo }
                                circuitos = nuevaLista
                                scheduleManager.guardarCircuitos(nuevaLista)
                            }
                            true
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = true,
                        enableDismissFromEndToStart = true,
                        backgroundContent = {
                            val colorBg = if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) Color.Blue else Color.Red
                            val icono = if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) Icons.Default.Edit else Icons.Default.Delete
                            val align = if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
                            
                            Box(
                                Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)).background(colorBg).padding(horizontal = 20.dp),
                                contentAlignment = align
                            ) {
                                Icon(icono, contentDescription = null, tint = Color.White)
                            }
                        }
                    ) {
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
                                
                                IconButton(onClick = {
                                    if (alarmaActivada) {
                                        AlarmaScheduler.cancelarAlarma(context, c.codigo)
                                        scheduleManager.desactivarAlarma(c.codigo)
                                        alarmasActivas = alarmasActivas - c.codigo
                                        Toast.makeText(context, "Alarma desactivada", Toast.LENGTH_SHORT).show()
                                    } else {
                                        AlarmaScheduler.programarAlarma(context, bloquesPorCircuito[c.codigo] ?: emptyList(), c.codigo)
                                        scheduleManager.activarAlarma(c.codigo)
                                        alarmasActivas = alarmasActivas + c.codigo
                                        Toast.makeText(context, "Alarma activada (15 min antes)", Toast.LENGTH_SHORT).show()
                                    }
                                }) {
                                    Icon(Icons.Default.Notifications, contentDescription = "Alarma", tint = if (alarmaActivada) Color(0xFF2196F3) else Color.Gray)
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
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogoAgregar) {
        SeleccionarCircuitoDialog(
            circuitosActuales = circuitos,
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
                
                val intent = Intent(context, CorrienteWidget::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, CorrienteWidget::class.java))
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                }
                context.sendBroadcast(intent)
                
                circuitoAEditar = null
            }
        )
    }
}

// --- ONBOARDING ---
@Composable
fun OnboardingScreen(onCircuitoSeleccionado: (Circuito) -> Unit) {
    var seleccionado by remember { mutableStateOf<Circuito?>(null) }

    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(40.dp))
        Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color(0xFFFFC107))
        Text("Bienvenido a Corriente", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Selecciona el circuito de tu casa para empezar:", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp, bottom = 24.dp))
        
        LazyColumn(Modifier.weight(1f)) {
            items(DatosIniciales.circuitos, key = { it.codigo }) { c ->
                Card(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { seleccionado = c },
                    colors = CardDefaults.cardColors(containerColor = if (seleccionado == c) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(16.dp).clip(CircleShape).background(Color(c.color)))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("${c.codigo} · ${c.nombre}", fontWeight = FontWeight.Bold)
                            Text(c.zona, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        Button(
            onClick = { seleccionado?.let { onCircuitoSeleccionado(it) } },
            enabled = seleccionado != null,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Comenzar a usar la app")
        }
    }
}

// --- DIÁLOGOS ---
@Composable
fun SeleccionarCircuitoDialog(
    circuitosActuales: List<Circuito>,
    onDismiss: () -> Unit,
    onAdd: (Circuito) -> Unit
) {
    var mostrarCreacionManual by remember { mutableStateOf(false) }
    val disponibles = DatosIniciales.circuitos.filter { base -> circuitosActuales.none { it.codigo == base.codigo } }

    if (mostrarCreacionManual) {
        AgregarCircuitoManualDialog(
            onDismiss = { mostrarCreacionManual = false },
            onAdd = { nuevo -> onAdd(nuevo) }
        )
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir Circuito") },
        text = {
            Column {
                if (disponibles.isEmpty()) {
                    Text("Ya tienes todos los circuitos predefinidos en tu lista.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    Text("Selecciona uno de la lista:", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))
                    LazyColumn(Modifier.heightIn(max = 300.dp)) {
                        items(disponibles, key = { it.codigo }) { c ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { onAdd(c) }.padding(vertical = 12.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(Modifier.size(12.dp).clip(CircleShape).background(Color(c.color)))
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text("${c.codigo} · ${c.nombre}", style = MaterialTheme.typography.titleSmall)
                                    Text(c.zona, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                }
                            }
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                OutlinedButton(onClick = { mostrarCreacionManual = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Crear uno nuevo manualmente")
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } }
    )
}

@Composable
fun AgregarCircuitoManualDialog(
    onDismiss: () -> Unit,
    onAdd: (Circuito) -> Unit
) {
    var codigo by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Circuito Personalizado") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = codigo, onValueChange = { codigo = it }, label = { Text("Código") }, singleLine = true)
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, singleLine = true)
            }
        },
        confirmButton = {
            TextButton(
                enabled = codigo.isNotBlank() && nombre.isNotBlank(),
                onClick = {
                    val color = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
                    onAdd(Circuito(codigo = codigo.trim().uppercase(), nombre = nombre.trim(), zona = "Personalizado", descripcion = "Añadido por el usuario", color = color))
                }
            ) { Text("Añadir") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun EditarHorarioDialog(
    circuito: Circuito,
    bloquesActuales: List<BloqueServicio>,
    onDismiss: () -> Unit,
    onSave: (List<BloqueServicio>) -> Unit
) {
    val context = LocalContext.current
    var listaEditada by remember { mutableStateOf(bloquesActu