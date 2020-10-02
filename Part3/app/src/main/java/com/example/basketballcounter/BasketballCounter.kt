package com.example.basketballcounter

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.util.*

private const val TAG = "BasketballCounter"

class BasketballCounter : AppCompatActivity() , BasketballCounterFragment.Callbacks, GameListFragment.Callbacks {


    override fun onDisplayClick(winnerA: Boolean) {
        val fragment = GameListFragment.newInstance(winnerA)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container_a, fragment)
            .addToBackStack(null)
            .commit()
        Log.d(TAG, "BasketballCounter displaying game list")
    }

    override fun onGameSelected(gameID: UUID) {
        val fragment = BasketballCounterFragment.newInstance(gameID)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container_a, fragment)
            .addToBackStack(null)
            .commit()
        Log.d(TAG, "BasketballCounter displaying detail game")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "BasketballCounter instance created")

        var currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_a)
        if (currentFragment == null) {
            val fragment =
                BasketballCounterFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container_a, fragment)
                .commit()
        }
    }

}