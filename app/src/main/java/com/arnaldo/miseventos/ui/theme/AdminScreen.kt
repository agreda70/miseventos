package com.arnaldo.miseventos.ui.theme


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arnaldo.miseventos.data.EventDao
import com.arnaldo.miseventos.data.EventType
import kotlinx.coroutines.launch

@Composable
fun AdminScreen(eventDao: EventDao, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    // Observamos los tipos de eventos desde la base de datos
    val eventTypes by eventDao.getAllEventTypes().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = onBack) {
            Text("← Volver al Calendario")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Administrar Tipos de Eventos", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre (ej: Gimnasio)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (name.isNotBlank()) {
                    val newType = EventType(name = name, iconName = "star", color = 0xFF6200EE)
                    scope.launch { eventDao.insertEventType(newType) }
                    name = "" // Limpiamos el campo
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Guardar Tipo de Evento")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Eventos Registrados:", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)

        LazyColumn {
            items(eventTypes) { type ->
                Text("• ${type.name}", modifier = Modifier.padding(4.dp))
            }
        }
    }
}