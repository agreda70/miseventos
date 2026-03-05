package com.arnaldo.miseventos.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(eventDao: EventDao, onBack: () -> Unit) {
    val allEvents by eventDao.getAllEvents().collectAsState(initial = emptyList())
    val eventTypes by eventDao.getAllEventTypes().collectAsState(initial = emptyList())

    // Filtramos los eventos del mes actual para la gráfica
    val currentYearMonth = YearMonth.from(LocalDate.now()).toString()
    val thisMonthEvents = allEvents.filter { it.eventDate.startsWith(currentYearMonth) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estadísticas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // --- SECCIÓN 1: Resumen General ---
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total Histórico", fontSize = 16.sp, color = Color.Gray)
                    Text(
                        text = "${allEvents.size} Eventos",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- SECCIÓN 2: Gráfica de este mes ---
            Text("Distribución de este mes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            if (thisMonthEvents.isEmpty()) {
                Text("Aún no hay datos este mes.", color = Color.Gray)
            } else {
                // Barra de progreso compuesta
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray)
                ) {
                    // Agrupamos los eventos de este mes por Tipo y dibujamos una porción de la barra
                    val groupedThisMonth = thisMonthEvents.groupBy { it.eventTypeId }

                    groupedThisMonth.forEach { (typeId, eventsOfType) ->
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

            // --- SECCIÓN 3: Desglose por Tipo (Histórico) ---
            Text("Desglose Histórico por Tipo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            val groupedAllTime = allEvents.groupBy { it.eventTypeId }

            LazyColumn {
                items(eventTypes) { type ->
                    val count = groupedAllTime[type.id]?.size ?: 0
                    if (count > 0) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(AppIcons.getIcon(type.iconName), contentDescription = null, tint = Color(type.color))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(type.name, fontSize = 16.sp)
                            }
                            Text(
                                text = count.toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                    }
                }
            }
        }
    }
}