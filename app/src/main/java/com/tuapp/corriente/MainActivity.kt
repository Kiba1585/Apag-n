package com.tuapp.corriente

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    ListaEstatica()
                }
            }
        }
    }
}

data class CircuitoFalso(val codigo: String, val nombre: String, val zona: String, val color: Long, val badge: String)

@Composable
fun ListaEstatica() {
    val lista = listOf(
        CircuitoFalso("A060", "Nueva Gerona Centro", "Nueva Gerona", 0xFFF44336, ""),
        CircuitoFalso("A070", "Pueblo Nuevo / Estadio", "Nueva Gerona", 0xFF9C27B0, "🏥"),
        CircuitoFalso("A080", "Soterrado – Hospital Héroes", "Nueva Gerona", 0xFF0D47A1, "🏥 Máxima"),
        CircuitoFalso("A010", "Mella Cítrico", "Mella", 0xFF4CAF50, "🏥 💧 🏫")
    )

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("⚡ Circuitos (Prueba)", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(lista) { c ->
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(14.dp).clip(CircleShape).background(Color(c.color)))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("${c.codigo} · ${c.nombre}", style = MaterialTheme.typography.titleSmall)
                            Text(c.zona, style = MaterialTheme.typography.labelSmall)
                            if (c.badge.isNotEmpty()) {
                                Text(c.badge, style = MaterialTheme.typography.labelSmall, color = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}