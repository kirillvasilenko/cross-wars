package viewModels.mainScreen

import Api
import model.*
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

    override suspend fun init() {
        window.alert("Init ept")
        resetGamesAndSubscribe()
    }

    override suspend fun dispose() {
        SubscriptionHub.unsubscribeFromCommonEvents()
    }

    private suspend fun resetGamesAndSubscribe(){
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
        SubscriptionHub.subscribeOnCommonEvents(::handleGameEvent, ::resetGamesAndSubscribe)
    }

    private suspend fun handleGameEvent(event: GameEvent){
        when(event){
            is GameStateChanged -> onGameStateChanged(event)
            is UserJoined -> onUserJoined(event)
            is UserLeaved -> onUserLeaved(event)
            is UserSubscribedOnGameEvents -> onUserSubscribedOnGameEvents(event)
            else -> log("game list received a strange event: $event")
        }
    }

    private suspend fun onGameStateChanged(event:GameStateChanged){
        val game = games.firstOrNull{ it.gameId == event.gameId }
        if(game == null){
            if(event.actualState == GameState.ACTIVE){
                addGame(event.gameId)
            }
            return
        }

        if(event.actualState == GameState.ACTIVE) return

        games.remove(game)
        raiseChanged()
    }


    private suspend fun addGame(gameId: Int){
        val gameVm = GamePreviewVm(Api.games.getGame(gameId))
        games.add(gameVm)
        raiseChanged()
    }

    private suspend fun onUserJoined(event:UserJoined){

    }

    private suspend fun onUserLeaved(event:UserLeaved){

    }

    private suspend fun onUserSubscribedOnGameEvents(event:UserSubscribedOnGameEvents){

    }

    private suspend fun raiseJoinedGame(game: GameDto){
        onJoinedGame(game)
    }
}


