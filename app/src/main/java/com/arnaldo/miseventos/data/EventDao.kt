package com.arnaldo.miseventos.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    // Para los Tipos de Eventos (Administración)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEventType(eventType: EventType)

    @Query("SELECT * FROM event_types")
    fun getAllEventTypes(): Flow<List<EventType>>

    // Para los Eventos
    @Insert
    suspend fun insertEvent(event: Event)

    @Query("SELECT * FROM events WHERE eventDate = :date")
    fun getEventsByDate(date: String): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE eventDate LIKE :yearMonth || '%'")
    fun getEventsByMonth(yearMonth: String): Flow<List<Event>>
}