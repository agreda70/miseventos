package com.arnaldo.miseventos.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEventType(eventType: EventType)

    @Update
    suspend fun updateEventType(eventType: EventType)

    @Delete
    suspend fun deleteEventType(eventType: EventType)

    // Esta consulta cuenta cuántos eventos usan un tipo específico
    @Query("SELECT COUNT(*) FROM events WHERE eventTypeId = :typeId")
    suspend fun countEventsWithType(typeId: Int): Int

    @Query("SELECT * FROM event_types")
    fun getAllEventTypes(): Flow<List<EventType>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEvent(event: Event)

    @Query("DELETE FROM events WHERE eventDate = :date AND eventTypeId = :typeId")
    suspend fun deleteEvent(date: String, typeId: Int)

    @Query("SELECT * FROM events WHERE eventDate LIKE :yearMonth || '%'")
    fun getEventsByMonth(yearMonth: String): Flow<List<Event>>
}