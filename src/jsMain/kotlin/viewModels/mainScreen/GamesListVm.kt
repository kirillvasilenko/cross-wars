package viewModels.mainScreen

import Api
import model.GameDto
import model.GameStarted
import viewModels.GameEventHandler
import viewModels.SubscriptionHub
import viewModels.ViewModel

class GamesListVm: ViewModel(){

    val startNewGameVm = child(StartNewGameVm())

    val games = mutableListOf<GamePreviewVm>()

    override suspend fun initImpl() {
        resetGamesAndSubscribe()
    }

    override suspend fun disposeImpl() {
        SubscriptionHub.unsubscribeFromGameStartedEvents()
    }

    private suspend fun resetGamesAndSubscribe(){
        games.forEach{ removeChild(it) }
        games.clear()
        Api.games.getActiveGames()
                .map { child(GamePreviewVm(it)) }
                .forEach { games.add(it) }
        onStateChanged()
        SubscriptionHub.subscribeOnGameStartedEvents(
                GameEventHandler(::handleGameStarted, ::resetGamesAndSubscribe)
        )
    }

    private fun filter(game:GameDto):Boolean{
        return true
    }

    private suspend fun handleGameStarted(event:GameStarted){
        if (games.any { it.gameId == event.gameId }) return

        val game = Api.games.getGame(event.gameId)
        if (!filter(game)) return

        games.add(GamePreviewVm(game))
        raiseStateChanged()
    }
}


