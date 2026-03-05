package com.arnaldo.miseventos.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "events",
    foreignKeys = [
        ForeignKey(
            entity = EventType::class,
            parentColumns = ["id"],
            childColumns = ["eventTypeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val eventDate: String, // Guardaremos la fecha como texto "YYYY-MM-DD" para facilitar el filtro en el calendario
    val eventTypeId: Int
)
