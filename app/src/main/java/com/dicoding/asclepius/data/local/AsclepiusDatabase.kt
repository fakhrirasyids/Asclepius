package com.dicoding.asclepius.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dicoding.asclepius.data.local.models.History

@Database(entities = [History::class], version = 1, exportSchema = false)
abstract class AsclepiusDatabase: RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}