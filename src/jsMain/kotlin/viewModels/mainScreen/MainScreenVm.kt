package viewModels.mainScreen

import model.GameDto
import model.UserDto
import viewModels.ViewModel

class MainScreenVm(val user: UserDto): ViewModel(){

    val gamesListVm = GamesListVm()

    var onJoinedGame: suspend (GameDto) -> Unit = {}

    init {
        gamesListVm.onJoinedGame = { onJoinedGame(it) }
    }

    override suspend fun dispose() {
        gamesListVm.dispose()
    }

}