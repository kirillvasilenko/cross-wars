package viewModels.mainScreen

import model.GameDto
import model.UserDto
import viewModels.ViewModel

class MainScreenVm(val user: UserDto): ViewModel(){

    val gamesListVm = GamesListVm()

    var onStartedNewGame: suspend (GameDto) -> Unit = {}

    init {
        gamesListVm.startNewGameVm.onExecuted = { newGame ->
            onStartedNewGame(newGame)
        }
    }

}