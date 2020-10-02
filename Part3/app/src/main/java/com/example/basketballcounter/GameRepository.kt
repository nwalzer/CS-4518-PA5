package com.example.basketballcounter

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.basketballcounter.database.GameDatabase
import java.io.File
import java.util.*
import java.util.concurrent.Executors

class GameRepository private constructor(context: Context) {
    private val database : GameDatabase = Room.databaseBuilder(
        context.applicationContext,
        GameDatabase::class.java,
        "game-database"
    ).build()

    private val gameDao = database.gameDao()
    private val executor = Executors.newSingleThreadExecutor()
    private val filesDir = context.applicationContext.filesDir

    companion object {
        private var INSTANCE: GameRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE =
                    GameRepository(context)
            }
        }

        fun get(): GameRepository {
            return INSTANCE
                ?:
            throw IllegalStateException("CrimeRepository must be initialized")
        }
    }

    fun getGames(): LiveData<List<Game>> = gameDao.getGames()

    fun getGame(id: UUID): LiveData<Game?> = gameDao.getGame(id)

    fun updateGame(game: Game) {
        executor.execute {
            gameDao.updateGame(game)
        }
    }

    fun addGame(game: Game) {
        executor.execute {
            gameDao.addGame(game)
        }
    }

    fun getPhotoFile(game: Game, isA: Boolean): File {
        return if(isA){
            File(filesDir, game.photoFileA)
        } else {
            File(filesDir, game.photoFileB)
        }
    }
}