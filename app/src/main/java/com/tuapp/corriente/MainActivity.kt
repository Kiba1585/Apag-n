package com.tuapp.corriente

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuapp.corriente.data.entity.Circuito
import com.tuapp.corriente.ui.LeyendaScreen
import com.tuapp.corriente.ui.MainViewModel

class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    AppRoot(vm)
                }
            }
        }
    }
}

@Composable
fun AppRoot(vm: MainViewModel) {
    var pantalla by remember { mutableStateOf("inicio") }
    val circuitos by vm.circuitos.collectAsStateWithLifecycle()

    when (pantalla) {
        "inicio" -> InicioScreen(circuitos, onLeyenda = { pantalla = "leyenda" })
        "leyenda" -> LeyendaScreen(circuitos, onVolver = { pantalla = "inicio" })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    circuitos: List<Circuito>,
    onLeyenda: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("⚡ Circuitos") },
                actions = {
                    IconButton(onClick = onLeyenda) {
                        Icon(Icons.Default.MenuBook, contentDescription = "Leyenda")
                    }
                }
            )
        }
    ) { padding ->
        if (circuitos.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Text("Cargando circuitos...")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(circuitos, key = { it.codigo }) { c ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(
                            Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color(c.color))
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    "${c.codigo} · ${c.nombre}",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    c.zona,
                                    style = MaterialTheme.typography.labelSmall
                                )
                                if (c.prioridadMaxima || c.tieneSalud || c.tieneBombeo) {
                                    Spacer(Modifier.height(4.dp))
                                    Row {
                                        if (c.prioridadMaxima) Badge("🏥 Máxima")
                                        if (c.tieneSalud) Badge("🏥")
                                        if (c.tieneBombeo) Badge("💧")
                                        if (c.tieneEducacion) Badge("🏫")
                                        if (c.tieneTelecom) Badge("📡")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Badge(texto: String) {
    Text(
        texto,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier
            .padding(end = 4.dp)
            .background(
                MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.shapes.small
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}