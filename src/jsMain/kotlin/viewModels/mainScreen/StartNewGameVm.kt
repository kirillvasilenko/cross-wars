package viewModels.mainScreen

import Api
import model.GameDto
import viewModels.common.CommandVm
import viewModels.common.ViewModel
import viewModels.common.VmEvent

class StartNewGameVm: CommandVm(){

    override suspend fun executeImpl() {
        Api.games.startNewGame()
    }

}