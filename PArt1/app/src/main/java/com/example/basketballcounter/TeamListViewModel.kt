package com.example.basketballcounter

import androidx.lifecycle.ViewModel

class TeamListViewModel: ViewModel() {
    private val gameRepository = GameRepository.get()
    val gameListLiveData = gameRepository.getGames()
}