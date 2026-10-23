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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuapp.corriente.ui.MainViewModel

class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    PantallaCircuito(vm)
                }
            }
        }
    }
}

@Composable
fun PantallaCircuito(vm: MainViewModel) {
    val circuitos by vm.circuitos.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("⚡ Circuitos (Memoria)", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        
        if (circuitos.isEmpty()) {
            Text("Cargando...", style = MaterialTheme.typography.bodyLarge)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(circuitos, key = { it.codigo }) { c ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(14.dp).clip(CircleShape).background(Color(c.color)))
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("${c.codigo} · ${c.nombre}", style = MaterialTheme.typography.titleSmall)
                                Text(c.zona, style = MaterialTheme.typography.labelSmall)
                                if (c.prioridadMaxima || c.tieneSalud || c.tieneBombeo) {
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "${if (c.prioridadMaxima) "🏥 Máxima " else ""}${if (c.tieneSalud) "🏥 " else ""}${if (c.tieneBombeo) "💧" else ""}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}