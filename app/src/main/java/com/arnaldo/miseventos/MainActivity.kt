package com.arnaldo.miseventos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arnaldo.miseventos.ui.theme.AdminScreen
import com.arnaldo.miseventos.ui.theme.CalendarScreen
import com.arnaldo.miseventos.ui.theme.MisEventosTheme
import com.arnaldo.miseventos.ui.theme.PinScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Obtenemos el acceso a la Base de Datos
        val db = (application as EventApp).database.eventDao()

        setContent {
            MisEventosTheme {
                var currentScreen by remember { mutableStateOf("auth") }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        "auth" -> {
                            PinScreen(onPinCorrect = { currentScreen = "calendar" })
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