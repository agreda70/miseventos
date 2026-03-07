package com.arnaldo.miseventos.ui.theme

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object MyInputDefaults {
    @Composable
    fun whiteColors() = OutlinedTextFieldDefaults.colors(
        // Colores del Texto
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        // Colores del Borde
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Color.White,
        // Colores de la Etiqueta (Label)
        focusedLabelColor = Color.White,
        unfocusedLabelColor = Color.White,
        cursorColor = Color.White
    )
}