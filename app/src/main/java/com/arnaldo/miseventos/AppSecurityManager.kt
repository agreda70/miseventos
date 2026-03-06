package com.arnaldo.miseventos
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Creamos una extensión para acceder a DataStore
val Context.dataStore by preferencesDataStore(name = "settings")

class AppSecurityManager(private val context: Context) {
    companion object {
        val PIN_KEY = stringPreferencesKey("user_pin")
        val QUESTION_KEY = stringPreferencesKey("security_question")
        val ANSWER_KEY = stringPreferencesKey("security_answer")
    }

    // Guardar los datos de configuración inicial
    suspend fun saveCredentials(pin: String, question: String, answer: String) {
        context.dataStore.edit { prefs ->
            prefs[PIN_KEY] = pin
            prefs[QUESTION_KEY] = question
            prefs[ANSWER_KEY] = answer
        }
    }

    suspend fun clearCredentials() {
        context.dataStore.edit { prefs ->
            prefs.clear() // Esto borra el PIN y las preguntas de seguridad
        }
    }

    // Obtener el PIN (Si es null, significa que es la primera vez que abre la app)
    val userPin: Flow<String?> = context.dataStore.data.map { it[PIN_KEY] }
    val securityQuestion: Flow<String?> = context.dataStore.data.map { it[QUESTION_KEY] }
    val securityAnswer: Flow<String?> = context.dataStore.data.map { it[ANSWER_KEY] }
}