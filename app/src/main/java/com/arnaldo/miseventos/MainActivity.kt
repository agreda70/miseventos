package com.arnaldo.miseventos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.arnaldo.miseventos.ui.theme.AdminScreen
import com.arnaldo.miseventos.ui.theme.CalendarScreen
import com.arnaldo.miseventos.ui.theme.PinScreen
import com.arnaldo.miseventos.ui.theme.MisEventosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializamos los dos pilares: Base de datos y Seguridad
        val db = (application as EventApp).database.eventDao()
        val securityManager = AppSecurityManager(this)

        setContent {
            MisEventosTheme {
                var currentScreen by remember { mutableStateOf("auth") }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        "auth" -> {
                            PinScreen(
                                securityManager = securityManager,
                                onAuthSuccess = { currentScreen = "calendar" }
                            )
                        }
                        "calendar" -> {
                            CalendarScreen(
                                eventDao = db,
                                onNavigateToAdmin = { currentScreen = "admin" }
                            )
                        }
                        "admin" -> {
                            AdminScreen(
                                eventDao = db,
                                onBack = { currentScreen = "calendar" }
                            )
                        }
                    }
                }
            }
        }
    }
}