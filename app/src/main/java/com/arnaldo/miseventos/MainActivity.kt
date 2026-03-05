package com.arnaldo.miseventos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.arnaldo.miseventos.ui.theme.MisEventosTheme
import com.arnaldo.miseventos.ui.theme.PinScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MisEventosTheme { // Usa el tema de tu app
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Llamamos a nuestra pantalla
                    PinScreen(
                        onPinCorrect = {
                            // Aquí pondremos el código para ir al Calendario más adelante
                            println("¡PIN CORRECTO! Yendo al calendario...")
                        }
                    )
                }
            }
        }
    }
}