package viewModels.mainScreen

import Api
import model.GameDto
import viewModels.SubscriptionHub
import viewModels.ViewModel

class GamesListVm: ViewModel(){

    val startNewGameVm = StartNewGameVm()

    val games = mutableListOf<GamePreviewVm>()

    var onJoinedGame: suspend (GameDto) -> Unit = {}

    init{
        startNewGameVm.onExecuted = ::raiseJoinedGame
    }

    override suspend fun init() {
        Api.games.getGames()
                .map {
                    GamePreviewVm(it)
                }
                .forEach {
                    it.onExecuted = ::raiseJoinedGame
                    games.add(it)
                }
        onChanged()
        SubscriptionHub.subscribeCommonEvents({}, {})
    }

    private suspend fun raiseJoinedGame(game: GameDto){
        onJoinedGame(game)
    }
}


