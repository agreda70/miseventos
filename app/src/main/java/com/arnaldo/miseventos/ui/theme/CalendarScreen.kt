package com.arnaldo.miseventos.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arnaldo.miseventos.data.Event
import com.arnaldo.miseventos.data.EventDao
import com.arnaldo.miseventos.data.EventType
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(eventDao: EventDao, onNavigateToAdmin: () -> Unit) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDialog by remember { mutableStateOf(false) }

    // El "scope" nos permite ejecutar tareas en segundo plano (como guardar en base de datos)
    val scope = rememberCoroutineScope()

    // 1. Traemos los Tipos de Eventos (Administración)
    val eventTypes by eventDao.getAllEventTypes().collectAsState(initial = emptyList())

    // 2. Traemos TODOS los eventos del mes actual que estamos viendo
    // Convertimos la fecha a formato "YYYY-MM" (ej: "2026-03") para buscar en la base de datos
    val yearMonthString = YearMonth.from(selectedDate).toString()
    val eventsOfThisMonth by eventDao.getEventsByMonth(yearMonthString).collectAsState(initial = emptyList())

    val monthName = selectedDate.month.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
    val year = selectedDate.year

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // --- CABECERA: MES, AÑO, RESUMEN Y BOTONES ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flecha Mes Anterior
            IconButton(onClick = { selectedDate = selectedDate.minusMonths(1) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Mes anterior")
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${monthName.replaceFirstChar { it.uppercase() }} $year",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                // Resumen dinámico: Cuenta cuántos eventos hay en eventsOfThisMonth
                Text(text = "Total eventos: ${eventsOfThisMonth.size}", color = Color.Gray, fontSize = 12.sp)
            }

            // Flecha Mes Siguiente
            IconButton(onClick = { selectedDate = selectedDate.plusMonths(1) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Mes siguiente")
            }

            // Botón Configuración
            IconButton(onClick = onNavigateToAdmin) {
                Icon(Icons.Default.Settings, contentDescription = "Administración")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- DÍAS DE LA SEMANA ---
        Row(modifier = Modifier.fillMaxWidth()) {
            val diasSemana = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
            diasSemana.forEach { dia ->
                Text(
                    text = dia, modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontWeight = FontWeight.Bold, fontSize = 12.sp
                )
            }
        }

        // --- CUADRÍCULA ---
        // Le pasamos a la cuadrícula los eventos y los tipos para que sepa qué dibujar
        CalendarGrid(
            date = selectedDate,
            events = eventsOfThisMonth,
            eventTypes = eventTypes,
            onDayClick = { day ->
                selectedDate = selectedDate.withDayOfMonth(day)
                showDialog = true // Mostramos el modal
            }
        )
    }

    // --- EL MODAL (Dialog) ---
    if (showDialog) {
        // Filtramos de todos los eventos del mes, SOLO los que coinciden con el día seleccionado
        val eventsOfSelectedDay = eventsOfThisMonth.filter { it.eventDate == selectedDate.toString() }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Día ${selectedDate.dayOfMonth} de ${monthName}") },
            text = {
                Column {
                    // SECCIÓN A: Mostrar los eventos que ya ocurrieron este día (Tu requerimiento)
                    if (eventsOfSelectedDay.isNotEmpty()) {
                        Text("Eventos registrados:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        eventsOfSelectedDay.forEach { event ->
                            // Buscamos el tipo de evento para saber su nombre y color
                            val type = eventTypes.find { it.id == event.eventTypeId }
                            if (type != null) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(type.color), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(type.name, fontSize = 14.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider() // Una línea separadora
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // SECCIÓN B: Agregar nuevo evento
                    Text("Registrar nuevo evento:", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (eventTypes.isEmpty()) {
                        Text("No hay tipos. Ve al engranaje ⚙️ para crear uno.", color = Color.Red)
                    } else {
                        LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                            items(eventTypes) { type ->
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .clickable {
                                            // ¡AQUÍ GUARDAMOS EN LA BASE DE DATOS!
                                            scope.launch {
                                                val newEvent = Event(
                                                    eventDate = selectedDate.toString(), // Guardamos "YYYY-MM-DD"
                                                    eventTypeId = type.id
                                                )
                                                eventDao.insertEvent(newEvent)
                                            }
                                            showDialog = false
                                        }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(type.color))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(type.name)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showDialog = false }) { Text("Cerrar") } }
        )
    }
}

@Composable
fun CalendarGrid(date: LocalDate, events: List<Event>, eventTypes: List<EventType>, onDayClick: (Int) -> Unit) {
    val yearMonth = YearMonth.from(date)
    val daysInMonth = yearMonth.lengthOfMonth()
    val dayOfWeekOffset = yearMonth.atDay(1).dayOfWeek.value - 1
    val today = LocalDate.now()

    LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.fillMaxSize()) {
        // Huecos vacíos antes del día 1
        items(dayOfWeekOffset) { Box(modifier = Modifier.padding(4.dp).aspectRatio(1f)) }

        // Los cuadritos de los días
        items(daysInMonth) { day ->
            val dayNumber = day + 1
            val cellDate = yearMonth.atDay(dayNumber)

            // Verificamos si es un día del futuro (Tu requerimiento)
            val isFuture = cellDate.isAfter(today)

            // Buscamos los eventos de este cuadrito en particular
            val eventsInThisDay = events.filter { it.eventDate == cellDate.toString() }

            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .aspectRatio(1f)
                    // Si es futuro es transparente, si es pasado/hoy tiene fondo gris
                    .background(if (isFuture) Color.Transparent else Color.LightGray.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small)
                    // Solo permite hacer clic si NO es futuro
                    .clickable(enabled = !isFuture) { onDayClick(dayNumber) },
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // El número del día
                    Text(
                        text = dayNumber.toString(),
                        fontSize = 14.sp,
                        color = if (isFuture) Color.LightGray else Color.Black,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    // Aquí dibujamos los iconos (hasta 3 para que no se desborde el cuadrito)
                    Row(modifier = Modifier.padding(top = 2.dp), horizontalArrangement = Arrangement.Center) {
                        eventsInThisDay.take(3).forEach { event ->
                            val type = eventTypes.find { it.id == event.eventTypeId }
                            if (type != null) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(type.color),
                                    modifier = Modifier.size(12.dp) // Icono chiquitito
                                )
                            }
                        }
                        // Si en un día hay más de 3 eventos, ponemos un "+"
                        if(eventsInThisDay.size > 3) {
                            Text("+", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}