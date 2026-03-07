package com.arnaldo.miseventos.ui.theme

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.arnaldo.miseventos.AppSecurityManager
import kotlinx.coroutines.launch

@Composable
fun PinScreen(
    securityManager: AppSecurityManager,
    onAuthSuccess: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    // Leemos los datos de DataStore
    val savedPin by securityManager.userPin.collectAsState(initial = null)
    val savedQuestion by securityManager.securityQuestion.collectAsState(initial = null)
    val savedAnswer by securityManager.securityAnswer.collectAsState(initial = null)

    var inputPin by remember { mutableStateOf("") }
    var inputQuestion by remember { mutableStateOf("") }
    var inputAnswer by remember { mutableStateOf("") }
    var isRecovering by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {

        if (savedPin == null) {
            // --- MODO CONFIGURACIÓN INICIAL ---
            Text("Configura tu acceso", style = MaterialTheme.typography.headlineMedium)
            OutlinedTextField(value = inputPin, onValueChange = { if(it.length <= 4) inputPin = it }, label = { Text("Crea un PIN de 4 dígitos") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword))
            OutlinedTextField(value = inputQuestion, onValueChange = { inputQuestion = it }, label = { Text("Pregunta de seguridad (ej: Nombre mascota)") })
            OutlinedTextField(value = inputAnswer, onValueChange = { inputAnswer = it }, label = { Text("Respuesta") })

            Button(onClick = {
                if(inputPin.length == 4 && inputQuestion.isNotBlank() && inputAnswer.isNotBlank()) {
                    scope.launch { securityManager.saveCredentials(inputPin, inputQuestion, inputAnswer) }
                }
            }) { Text("Empezar a usar la app") }

        } else if (isRecovering) {
            // --- MODO RECUPERACIÓN ---
            Text("Recuperar PIN", style = MaterialTheme.typography.headlineSmall)
            Text(savedQuestion ?: "")
            OutlinedTextField(value = inputAnswer, onValueChange = { inputAnswer = it }, label = { Text("Tu respuesta") })
            Button(onClick = {
                if(inputAnswer.trim().equals(savedAnswer?.trim(), ignoreCase = true)) {
                    Toast.makeText(context, "Tu PIN es: $savedPin", Toast.LENGTH_LONG).show()
                    isRecovering = false
                }
            }) { Text("Verificar") }
            TextButton(onClick = { isRecovering = false }) { Text("Volver") }

        } else {
            // --- MODO LOGIN NORMAL ---
            Text("Ingresa tu PIN", style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(value = inputPin, onValueChange = {
                inputPin = it
                if(it == savedPin) onAuthSuccess()
            }, label = { Text("PIN") }, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword))

            TextButton(onClick = { isRecovering = true }) { Text("Olvidé mi PIN") }
        }
    }
}