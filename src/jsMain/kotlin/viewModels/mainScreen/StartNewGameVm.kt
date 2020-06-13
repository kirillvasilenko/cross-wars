package viewModels.mainScreen

import Api
import model.GameDto
import viewModels.CommandVm

class StartNewGameVm: CommandVm<GameDto>(){

    override suspend fun executeImpl() : GameDto {
        return Api.games.startNewGame()
    }

}