package com.example.basketballcounter

import android.app.Application
import android.util.Log
import java.util.*
import kotlin.random.Random

private const val TAG = "BasketballCounter"

class BasketballCounterApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        GameRepository.initialize(this)
        Log.d(TAG, "GameRepo Initialized")
        //addGames(150)
    }

    private fun addGames(amount: Int){
        val repo = GameRepository.get()
        val r = Random(0)
        for(i in 0..amount){
            val newGame = Game()
            newGame.teamAScore = r.nextInt(120)
            newGame.teamBScore = r.nextInt(120)
            newGame.date = Calendar.getInstance().timeInMillis
            repo.addGame(newGame)
        }
        Log.d(TAG, "Added $amount Games to DB")
    }
}