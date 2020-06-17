package viewModels.mainScreen

import Api
import model.GameDto
import viewModels.common.CommandVm
import viewModels.common.ViewModel
import viewModels.common.VmEvent

class NewGameStarted(source: ViewModel, val game: GameDto): VmEvent(source)

class StartNewGameVm: CommandVm(){

    override suspend fun executeImpl() : VmEvent {
        val game = Api.games.startNewGame()
        return NewGameStarted(this, game)
    }

}