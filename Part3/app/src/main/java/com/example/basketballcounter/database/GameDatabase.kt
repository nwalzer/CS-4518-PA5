package com.example.basketballcounter.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.basketballcounter.Game

@Database(entities = [ Game::class ], version=1)
@TypeConverters(GameTypeConverters::class)
abstract class GameDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}