package com.arnaldo.miseventos.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [EventType::class, Event::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}