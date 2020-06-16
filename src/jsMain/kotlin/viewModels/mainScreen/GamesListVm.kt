package viewModels.mainScreen

import Api
import model.*
import viewModels.GameEventHandler
import viewModels.SubscriptionHub
import viewModels.ViewModel
import viewModels.log
import kotlin.browser.window

class GamesListVm: ViewModel(){

    val startNewGameVm = StartNewGameVm()

    val games = mutableListOf<GamePreviewVm>()

    var onJoinedGame: suspend (GameDto) -> Unit = {}

    init{
        startNewGameVm.onExecuted = ::raiseJoinedGame
    }

    override suspend fun initImpl() {
        resetGamesAndSubscribe()
    }

    override suspend fun dispose() {
        SubscriptionHub.unsubscribeFromGameStartedEvents()
    }

    private suspend fun resetGamesAndSubscribe(){
        games.forEach { it.dispose() }
        games.clear()
        Api.games.getActiveGames()
                .map {
                    GamePreviewVm(it)
                }
                .forEach {
                    it.onExecuted = ::raiseJoinedGame
                    games.add(it)
                }
        onChanged()
        SubscriptionHub.subscribeOnGameStartedEvents(
                GameEventHandler(::handleGameStarted, ::resetGamesAndSubscribe)
        )
    }

    private fun filter(game:GameDto):Boolean{
        return true
    }

    private suspend fun handleGameStarted(event:GameStarted){
        if(games.any{it.gameId == event.gameId}) return

        val game = Api.games.getGame(event.gameId)
        if(!filter(game)) return

        games.add(GamePreviewVm(game))
        log("gamesListVm: game started $event")
        raiseChanged()
    }

    private suspend fun raiseJoinedGame(game: GameDto){
        onJoinedGame(game)
    }
}


