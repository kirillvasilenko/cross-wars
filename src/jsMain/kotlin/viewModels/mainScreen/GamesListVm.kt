package viewModels.mainScreen

import api.Api
import model.GameDto
import model.GameStarted
import viewModels.GameEventHandler
import viewModels.SubscriptionHub
import viewModels.common.ViewModel
import viewModels.common.VmEvent

class UserStartedNewGame(source: ViewModel, val gameId: Int): VmEvent(source)

class GamesListVm(private val currentUserId: Int): ViewModel(){

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
                .map { child(GamePreviewVm(currentUserId, it)) }
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
        if(event.userId == currentUserId)
            raiseEvent(UserStartedNewGame(this, event.gameId))

        if (games.any { it.gameId == event.gameId }) return

        val game = Api.games.getGame(event.gameId)
        if (!filter(game)) return

        games.add(child(GamePreviewVm(currentUserId, game)))
        raiseStateChanged()
    }
}


