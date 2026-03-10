package com.arnaldo.miseventos.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arnaldo.miseventos.data.EventDao
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(eventDao: EventDao, onBack: () -> Unit) {
    val allEvents by eventDao.getAllEvents().collectAsState(initial = emptyList())
    val eventTypes by eventDao.getAllEventTypes().collectAsState(initial = emptyList())

    // 1. ESTADO DEL RANGO DE FECHAS (Por defecto: Mes Actual)
    val today = LocalDate.now()
    var startDate by remember { mutableStateOf(today.withDayOfMonth(1)) }
    var endDate by remember { mutableStateOf(today.withDayOfMonth(today.lengthOfMonth())) }

    // Controla si se muestra el modal del calendario
    var showDatePicker by remember { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // 2. FILTRO INTELIGENTE: Solo los eventos dentro del rango seleccionado
    val filteredEvents = allEvents.filter { event ->
        val eventDate = LocalDate.parse(event.eventDate)
        !eventDate.isBefore(startDate) && !eventDate.isAfter(endDate)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- TARJETA DEL SELECTOR DE RANGO ---
        Card(
            modifier = Modifier.background(Color(0x10FFFFFF)).fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Rango de análisis:", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    Text(
                        text = "${startDate.format(formatter)} - ${endDate.format(formatter)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Cambiar fechas", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- TARJETA DEL TOTAL DE EVENTOS ---
        Card(
            modifier = Modifier.background(Color(0x10FFFFFF)).fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Eventos en este periodo", fontSize = 16.sp, color = Color.Gray)
                Text(
                    text = if (filteredEvents.size == 1) "1 Evento" else "${filteredEvents.size} Eventos",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- GRÁFICA PROPORCIONAL ---
        Text("Distribución", style = MaterialTheme.typography.titleMedium, color = Color.LightGray, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        if (filteredEvents.isEmpty()) {
            Text("No hay eventos registrados en este rango.", color = Color.Gray)
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray.copy(alpha = 0.5f))
            ) {
                val grouped = filteredEvents.groupBy { it.eventTypeId }
                grouped.forEach { (typeId, eventsOfType) ->
                    val type = eventTypes.find { it.id == typeId }
                    if (type != null) {
                        val weight = eventsOfType.size.toFloat()
                        Box(
                            modifier = Modifier
                                .weight(weight)
                                .fillMaxHeight()
                                .background(Color(type.color))
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- DESGLOSE POR TIPO EN LISTA ---
        Text("Detalle por Tipo", style = MaterialTheme.typography.titleMedium, color = Color.LightGray, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        val groupedByType = filteredEvents.groupBy { it.eventTypeId }

        LazyColumn {
            items(eventTypes) { type ->
                val count = groupedByType[type.id]?.size ?: 0
                if (count > 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Cuadrito con el icono
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(type.color).copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(AppIcons.getIcon(type.iconName), contentDescription = null, tint = Color(type.color), modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(type.name, fontSize = 16.sp, color = Color.LightGray, fontWeight = FontWeight.Medium)
                        }
                        Text(
                            text = count.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.LightGray
                        )
                    }
                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                }
            }
        }
    }

    // --- DIÁLOGO DEL SELECTOR DE RANGO (DateRangePicker) ---
    if (showDatePicker) {
        val dateRangePickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = startDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli(),
            initialSelectedEndDateMillis = endDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val startMillis = dateRangePickerState.selectedStartDateMillis
                    val endMillis = dateRangePickerState.selectedEndDateMillis
                    if (startMillis != null && endMillis != null) {
                        // Convertimos los milisegundos de vuelta a LocalDate
                        startDate = Instant.ofEpochMilli(startMillis).atZone(ZoneId.of("UTC")).toLocalDate()
                        endDate = Instant.ofEpochMilli(endMillis).atZone(ZoneId.of("UTC")).toLocalDate()
                    }
                    showDatePicker = false
                }) {
                    Text("Aplicar Rango")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.weight(1f),
                title = { Text("Selecciona el rango", modifier = Modifier.padding(16.dp)) },
                headline = { Text("Fechas de análisis", modifier = Modifier.padding(horizontal = 16.dp)) },
                showModeToggle = true
            )
        }
    }
}