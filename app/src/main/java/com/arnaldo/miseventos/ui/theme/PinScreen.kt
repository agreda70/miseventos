package com.arnaldo.miseventos.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun PinScreen(onPinCorrect: () -> Unit) {
    // Aquí guardamos lo que el usuario va escribiendo. Empieza vacío ("").
    var pin by remember { mutableStateOf("") }
    // Aquí guardamos si hay un error (ej: si puso mal el PIN)
    var isError by remember { mutableStateOf(false) }

    // El PIN correcto por ahora lo dejaremos fijo para probar.
    // Más adelante lo guardaremos en la memoria del teléfono.
    val pinCorrecto = "1234"

    // Column organiza los elementos de arriba hacia abajo
    Column(
        modifier = Modifier
            .fillMaxSize() // Ocupa toda la pantalla
            .padding(16.dp), // Deja un margen en los bordes
        horizontalAlignment = Alignment.CenterHorizontally, // Centra a lo ancho
        verticalArrangement = Arrangement.Center // Centra a lo alto
    ) {
        // Título de la pantalla
        Text(
            text = "Bienvenido a Mis Eventos",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(32.dp)) // Un espacio en blanco

        // El campo donde el usuario escribe
        OutlinedTextField(
            value = pin,
            onValueChange = { nuevoTexto ->
                pin = nuevoTexto // Actualiza el texto cada vez que teclea
                isError = false // Si teclea de nuevo, quitamos el error
            },
            label = { Text("Ingresa tu PIN") },
            // Esto hace que salga el teclado numérico
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            // Esto transforma los números en puntitos (****)
            visualTransformation = PasswordVisualTransformation(),
            isError = isError,
            singleLine = true
        )

        if (isError) {
            Text(text = "PIN incorrecto, intenta de nuevo.", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // El botón para entrar
        Button(
            onClick = {
                // ¿Qué pasa al hacer clic? Verificamos el PIN
                if (pin == pinCorrecto) {
                    onPinCorrect() // Si es correcto, ejecutamos la acción para ir al Calendario
                } else {
                    isError = true // Si es incorrecto, mostramos el error
                    pin = "" // Borramos lo que escribió para que vuelva a intentar
                }
            }
        ) {
            Text("Entrar")
        }
    }
}