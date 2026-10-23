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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tuapp.corriente.data.DatosIniciales

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    PantallaCircuitoReal()
                }
            }
        }
    }
}

@Composable
fun PantallaCircuitoReal() {
    // Cargamos los datos reales directamente, sin ViewModel ni Base de Datos
    val circuitos = remember { DatosIniciales.circuitos }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("⚡ Circuitos Reales", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        
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