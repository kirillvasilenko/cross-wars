package viewModels.mainScreen

import api.Api
import viewModels.common.CommandVm

class StartNewGameVm: CommandVm(){

    override suspend fun executeImpl() {
        Api.games.startNewGame()
    }

}