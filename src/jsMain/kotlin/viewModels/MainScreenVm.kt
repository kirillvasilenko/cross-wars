package viewModels

import Api
import model.GameDto
import model.UserDto

class MainScreenVm(val user: UserDto): ViewModel(){

    val gamesListVm = GamesListVm()

    var onStartedNewGame: (GameDto) -> Unit = {}

    init {
        gamesListVm.startNewGameVm.onExecuted = { newGame ->
            onStartedNewGame(newGame)
        }
    }

}