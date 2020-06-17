package viewModels.mainScreen

import Api
import model.GameDto
import viewModels.CommandVm
import viewModels.ViewModel
import viewModels.VmEvent

class NewGameStarted(source: ViewModel, val game: GameDto): VmEvent(source)

class StartNewGameVm: CommandVm(){

    override suspend fun executeImpl() : VmEvent {
        val game = Api.games.startNewGame()
        return NewGameStarted(this, game)
    }

}