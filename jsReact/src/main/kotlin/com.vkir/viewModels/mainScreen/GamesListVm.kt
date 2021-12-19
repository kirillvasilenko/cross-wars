package com.vkir.viewModels.mainScreen

import com.vkir.api.Api
import com.vkir.model.GameDto
import com.vkir.model.GameStarted
import com.vkir.viewModels.BackendEventsHandler
import com.vkir.viewModels.SubscriptionHub
import com.vkir.viewModels.common.ViewModel

class GamesListVm: ViewModel(){

    val startNewGameVm = StartNewGameVm()

    val games = mutableListOf<GamePreviewVm>()

    private val startedGameEventsListener = BackendEventsHandler(::handleGameStarted, ::resetGamesAndSubscribe)

    override suspend fun initImpl() {
        resetGamesAndSubscribe()
    }

    override suspend fun disposeImpl() {
        SubscriptionHub.unsubscribeFromGameStartedEvents(startedGameEventsListener)
        games.forEach{ it.dispose() }
    }

    private suspend fun resetGamesAndSubscribe(){
        games.forEach{ it.dispose() }
        games.clear()
        Api.games.getActiveGames()
                .map { GamePreviewVm(it) }
                .forEach { games.add(it) }

        raiseStateChanged()
        SubscriptionHub.subscribeOnGameStartedEvents(startedGameEventsListener)
    }

    private fun filter(game: GameDto):Boolean{
        return true
    }

    private suspend fun handleGameStarted(event: GameStarted){
        if (games.any { it.gameId == event.gameId }) return

        val game = Api.games.getGame(event.gameId)
        if (!filter(game)) return

        games.add(GamePreviewVm(game))
        raiseStateChanged()
    }
}


