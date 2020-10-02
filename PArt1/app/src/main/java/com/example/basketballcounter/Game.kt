package com.example.basketballcounter

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity (tableName = "table_game")
data class Game (@PrimaryKey val id: UUID = UUID.randomUUID(), var teamAName: String = "Team A", var teamBName: String = "Team B", var teamAScore: Int = 0, var teamBScore: Int = 0, var date: Long = 0){
    val photoFileA
        get() = "IMG_A_$id.jpg"
    val photoFileB
        get() = "IMG_B_$id.jpg"

    fun AisWinning(): Boolean{
        return teamAScore >= teamBScore
    }

    fun increasePoints(amount: Int, isA: Boolean){
        if(isA){
            teamAScore += amount
        } else {
            teamBScore += amount
        }
    }

    fun reset(){
        teamAScore = 0
        teamBScore = 0
    }
}