package com.jobaer.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jobaer.example.models.Match
import com.jobaer.example.models.Team

@Database(entities = [Team::class, Match::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun teamDao(): TeamDao
    abstract fun matchDao(): MatchDao

    companion object {
        const val DATABASE_NAME = "kickoff-db"
    }
}

