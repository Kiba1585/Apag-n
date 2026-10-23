package com.tuapp.corriente

import android.Manifest
import android.app.TimePickerDialog
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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

class MainActivity : ComponentActivity() {
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Si el usuario acepta o rechaza, la app sigue funcionando
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Pedir permiso de notificaciones en Android 13+
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

@Composable
fun PantallaEnVivo() {
    val context = LocalContext.current
    val scheduleManager = remember { ScheduleManager(context) }
    val circuitos = remember { DatosIniciales.circuitos }
    
    var todosLosBloques by remember { mutableStateOf(scheduleManager.cargarBloques() ?: DatosBloques.generarBloques()) }
    var favorito by remember { mutableStateOf(scheduleManager.obtenerCircuitoFavorito() ?: "A060") }
    
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
                    Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(14.dp).clip(CircleShape).background(Color(c.color)))
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text("${c.codigo} · ${c.nombre}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(textoEstado, style = MaterialTheme.typography.labelMedium, color = colorEstado, fontWeight = FontWeight.Bold)
                        }
                        
                        // Botón Alarma (Campana)
                        IconButton(onClick = {
                            AlarmaScheduler.programarAlarma(context, bloquesDeEsteCircuito, c.codigo)
                            // Feedback visual (puedes cambiar esto por un Toast si prefieres)
                            android.widget.Toast.makeText(context, "Alarma programada 15 min antes del próximo turno", android.widget.Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Alarma", tint = Color.Gray)
                        }

                        // Botón Favorito (Estrella)
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