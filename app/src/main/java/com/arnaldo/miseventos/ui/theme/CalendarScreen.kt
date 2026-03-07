package com.arnaldo.miseventos.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Settings
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
fun CalendarScreen(eventDao: EventDao, onNavigateToAdmin: () -> Unit, onNavigateToStats: () -> Unit) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val eventTypes by eventDao.getAllEventTypes().collectAsState(initial = emptyList())
    val yearMonthString = YearMonth.from(selectedDate).toString()
    val eventsByMonth by eventDao.getEventsByMonth(yearMonthString).collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.background(Color(0x80FFFFFF), RoundedCornerShape(8.dp)).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { selectedDate = selectedDate.minusMonths(1) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Mes anterior")
            }
            Text(
                text = "${selectedDate.month.getDisplayName(TextStyle.FULL, Locale("es", "ES")).uppercase()} ${selectedDate.year}",
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = { selectedDate = selectedDate.plusMonths(1) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Mes siguiente")
            }
            IconButton(onClick = onNavigateToStats) { // <--- NUEVO BOTÓN
                Icon(androidx.compose.material.icons.Icons.Default.BarChart, "Estadísticas")
            }
            IconButton(onClick = onNavigateToAdmin) {
                Icon(Icons.Default.Settings, "Administrar")
            }
        }

        // Tarjetas de resumen del mes
        LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
            items(eventTypes) { type ->
                val count = eventsByMonth.count { it.eventTypeId == type.id }
                if (count > 0) {
                    Card(
                        modifier = Modifier.padding(end = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(type.color).copy(alpha = 0.1f)),
                        border = BorderStroke(1.dp, Color(0x33FFFFFF))
                    ) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(AppIcons.getIcon(type.iconName), null, modifier = Modifier.size(16.dp), tint = Color(type.color))
                            Text(" ${type.name}: $count", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        // Días de la semana
        Row(modifier = Modifier.background(Color(0x33FFFFFF), RoundedCornerShape(8.dp)).fillMaxWidth()) {
            val diasSemana = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
            diasSemana.forEach { dia ->
                Text(
                    text = dia,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        // Cuadrícula del mes
        CalendarGrid(
            date = selectedDate,
            events = eventsByMonth,
            eventTypes = eventTypes
        ) { day ->
            selectedDate = selectedDate.withDayOfMonth(day)
            showDialog = true
        }
    }

    // Modal de selección múltiple (Checkboxes)
    if (showDialog) {
        val eventsToday = eventsByMonth.filter { it.eventDate == selectedDate.toString() }
        AlertDialog(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
            onDismissRequest = { showDialog = false },
            title = { Text("Eventos del día ${selectedDate.dayOfMonth}") },
            text = {
                LazyColumn {
                    items(eventTypes) { type ->
                        val isSelected = eventsToday.any { it.eventTypeId == type.id }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch {
                                        if (isSelected) {
                                            eventDao.deleteEvent(selectedDate.toString(), type.id)
                                        } else {
                                            eventDao.insertEvent(Event(eventDate = selectedDate.toString(), eventTypeId = type.id))
                                        }
                                    }
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(checked = isSelected, onCheckedChange = null)
                            Icon(AppIcons.getIcon(type.iconName), null, tint = Color(type.color))
                            Text(" ${type.name}", modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showDialog = false }) { Text("Listo") }
            }
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
        items(dayOfWeekOffset) {
            Box(modifier = Modifier.padding(2.dp).aspectRatio(1f))
        }

        items(daysInMonth) { day ->
            val dayNumber = day + 1
            val cellDate = yearMonth.atDay(dayNumber)
            val isFuture = cellDate.isAfter(today)
            val eventsInThisDay = events.filter { it.eventDate == cellDate.toString() }

            DayCell(
                day = dayNumber,
                isFuture = isFuture,
                events = eventsInThisDay,
                eventTypes = eventTypes,
                onClick = { onDayClick(dayNumber) }
            )
        }
    }
}

@Composable
fun DayCell(day: Int, isFuture: Boolean, events: List<Event>, eventTypes: List<EventType>, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(2.dp)
            .aspectRatio(1f)
            .background(
                color = if (isFuture) Color(0x33FFFFFF) else Color(0x33000000),
                shape = MaterialTheme.shapes.small
            )
            .clickable(enabled = !isFuture) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.toString(),
                fontSize = 12.sp,
                color = Color.Black
            )

            if (events.isNotEmpty()) {
                if (events.size == 1) {
                    val type = eventTypes.find { it.id == events[0].eventTypeId }
                    type?.let {
                        Icon(AppIcons.getIcon(it.iconName), null, modifier = Modifier.size(18.dp), tint = Color(it.color))
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .border(BorderStroke(1.dp, Color(0x10FFFFFF)))
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(5.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = events.size.toString(),
                            color = Color.White,
                            fontSize = if (isFuture) 10.sp else 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}