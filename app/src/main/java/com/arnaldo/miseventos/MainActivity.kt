package com.arnaldo.miseventos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.arnaldo.miseventos.ui.theme.AdminScreen
import com.arnaldo.miseventos.ui.theme.CalendarScreen
import com.arnaldo.miseventos.ui.theme.PinScreen
import com.arnaldo.miseventos.ui.theme.StatsScreen
import com.arnaldo.miseventos.ui.theme.MisEventosTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class) // Necesario para el TopAppBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = (application as EventApp).database.eventDao()
        val securityManager = AppSecurityManager(this)

        setContent {
            MisEventosTheme {
                var currentScreen by remember { mutableStateOf("auth") }
                val scope = rememberCoroutineScope()

                // 1. CREAMOS EL FONDO: Un gradiente suave de azul claro a blanco
                val backgroundBrush = Brush.verticalGradient(
                    //colors = listOf(Color(0xFF13BE6E), Color(0xFF2C84F7))
                    colors = listOf(Color(0xFF090979), Color(0xFF020024))
                )

                // 2. SCAFFOLD: Nos permite colocar la barra superior fácilmente
                Scaffold(
                    topBar = {
                        // Solo mostramos la barra si NO estamos en la pantalla del PIN
                        if (currentScreen != "auth") {
                            TopAppBar(
                                title = { Text("Mis Eventos", color = Color.White, fontWeight = FontWeight.Bold) },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Color.Transparent // Transparente para ver el fondo
                                ),
                                navigationIcon = {
                                    if (currentScreen != "calendar") {
                                        IconButton(onClick = { currentScreen = "calendar" }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Volver",
                                                tint = Color.White
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }
                ) { paddingValues ->
                    // 3. CAJA CON EL FONDO
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(backgroundBrush) // Aplicamos el fondo a toda la app
                            .padding(paddingValues) // Respeta el espacio de la barra superior
                    ) {
                        Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
                            when (screen) {
                                "auth" -> PinScreen(securityManager) { currentScreen = "calendar" }
                                "calendar" -> CalendarScreen(db, { currentScreen = "admin" }, { currentScreen = "stats" })
                                "admin" -> AdminScreen(
                                    eventDao = db,
                                    onBack = { currentScreen = "calendar" },
                                    onResetPin = {
                                        scope.launch {
                                            securityManager.clearCredentials()
                                            currentScreen = "auth"
                                        }
                                    }
                                )
                                "stats" -> StatsScreen(db) { currentScreen = "calendar" }
                            }
                        }
                    }
                }
            }
        }
    }
}