package com.arnaldo.miseventos.ui.theme


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arnaldo.miseventos.data.EventDao
import com.arnaldo.miseventos.data.EventType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(eventDao: EventDao, onBack: () -> Unit, onResetPin: () -> Unit) {
    val scope = rememberCoroutineScope()
    val eventTypes by eventDao.getAllEventTypes().collectAsState(initial = emptyList())
    val context = LocalContext.current

    // Estados para el formulario
    var name by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var selectedIcon by remember { mutableStateOf("Estrella") }
    var selectedColor by remember { mutableStateOf(AppColors.selection[0]) }
    var editingType by remember { mutableStateOf<EventType?>(null) } // Si no es null, estamos editando

    Column(
        modifier = Modifier.fillMaxSize()
            .background(Color.Transparent)
            .padding(16.dp)) {

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())

        // Selector de Iconos (Fila con scroll)
        Text("Selecciona un icono:", modifier = Modifier.padding(top = 8.dp))
        LazyRow {
            items(AppIcons.list.keys.toList()) { iconKey ->
                IconButton(
                    onClick = { selectedIcon = iconKey },
                    modifier = Modifier.background(if (selectedIcon == iconKey) Color.LightGray else Color.Transparent, shape = CircleShape)
                ) { Icon(AppIcons.getIcon(iconKey), contentDescription = null) }
            }
        }

        // Selector de Colores
        Text("Selecciona un color:")
        LazyRow {
            items(AppColors.selection) { colorLong ->
                Box(
                    modifier = Modifier.size(40.dp).padding(4.dp).background(Color(colorLong), CircleShape)
                        .clickable { selectedColor = colorLong }
                        .border(if (selectedColor == colorLong) 3.dp else 0.dp, Color.Black, CircleShape)
                )
            }
        }

        Button(
            onClick = {
                if (name.isNotBlank()) {
                    val type = EventType(id = editingType?.id ?: 0, name = name, iconName = selectedIcon, color = selectedColor)
                    scope.launch {
                        eventDao.insertEventType(type) // Insert o Update automático por el ID
                        name = ""; editingType = null
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) { Text(if (editingType == null) "Guardar" else "Actualizar") }

        if (editingType != null) {
            TextButton(onClick = { editingType = null; name = "" }, modifier = Modifier.fillMaxWidth()) { Text("Cancelar edición") }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // --- NUEVA SECCIÓN DE SEGURIDAD ---
        Text("Seguridad", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = { showConfirmDialog = true }, // <--- AHORA ABRE EL DIÁLOGO
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
        ) {
            Icon(Icons.Default.LockReset, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cambiar PIN y Pregunta de Seguridad")
        }

        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("¿Estás seguro?") },
                text = { Text("Esto borrará tu PIN actual y la aplicación se cerrará. Tendrás que configurar uno nuevo al volver a entrar.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showConfirmDialog = false
                            onResetPin() // Aquí sí ejecutamos el borrado
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Sí, borrar PIN")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        // Aquí empieza tu LazyColumn de tipos de eventos...

        // LISTA CRUD
        LazyColumn {
            items(eventTypes) { type ->
                ListItem(
                    headlineContent = { Text(type.name) },
                    leadingContent = { Icon(AppIcons.getIcon(type.iconName), null, tint = Color(type.color)) },
                    trailingContent = {
                        Row {
                            IconButton(onClick = {
                                editingType = type
                                name = type.name
                                selectedIcon = type.iconName
                                selectedColor = type.color
                            }) { Icon(Icons.Default.Edit, "Editar") }

                            IconButton(onClick = {
                                scope.launch {
                                    val count = eventDao.countEventsWithType(type.id)
                                    if (count == 0) eventDao.deleteEventType(type)
                                    else Toast.makeText(context, "No se puede borrar: tiene registros", Toast.LENGTH_SHORT).show()
                                }
                            }) { Icon(Icons.Default.Delete, "Borrar", tint = Color.Red) }
                        }
                    }
                )
            }
        }
    }
}