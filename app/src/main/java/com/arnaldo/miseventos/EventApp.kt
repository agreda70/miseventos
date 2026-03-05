package com.arnaldo.miseventos

import android.app.Application
import androidx.room.Room
import com.arnaldo.miseventos.data.AppDatabase

class EventApp : Application() {
    val database by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, "eventos_db")
            .fallbackToDestructiveMigration()
            .build()
    }
}