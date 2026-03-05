package com.arnaldo.miseventos.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [EventType::class, Event::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}