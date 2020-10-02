package com.example.basketballcounter.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.basketballcounter.Game
import java.util.*

@Dao
interface GameDao {
    @Query("SELECT * FROM table_game")
    fun getGames(): LiveData<List<Game>>

    @Query("SELECT * FROM table_game WHERE id=(:id)")
    fun getGame(id: UUID): LiveData<Game?>

    @Update
    fun updateGame(game: Game)

    @Insert
    fun addGame(game: Game)
}