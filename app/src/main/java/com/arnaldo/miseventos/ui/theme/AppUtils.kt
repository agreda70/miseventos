package com.arnaldo.miseventos.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

object AppIcons {
    // Lista de iconos que le propondremos al usuario
    val list = mapOf(
        "Gimnasio" to Icons.Default.FitnessCenter,
        "Comida" to Icons.Default.Restaurant,
        "Trabajo" to Icons.Default.Work,
        "Salud" to Icons.Default.MedicalServices,
        "Escuela" to Icons.Default.School,
        "Compras" to Icons.Default.ShoppingCart,
        "Dinero" to Icons.Default.AttachMoney,
        "Viaje" to Icons.Default.Flight,
        "Estrella" to Icons.Default.Star,
        "Corazón" to Icons.Default.Favorite
    )

    fun getIcon(name: String): ImageVector = list[name] ?: Icons.Default.Circle
}

object AppColors {
    val selection = listOf(
        0xFFE91E63, // Rosa
        0xFF9C27B0, // Morado
        0xFF2196F3, // Azul
        0xFF4CAF50, // Verde
        0xFFFFEB3B, // Amarillo
        0xFFFF9800, // Naranja
        0xFF795548  // Café
    )
}