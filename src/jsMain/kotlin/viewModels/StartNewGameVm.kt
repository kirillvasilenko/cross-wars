package viewModels

import Api
import model.GameDto

class StartNewGameVm: CommandVm<GameDto>(){
    
    override suspend fun executeImpl() : GameDto {
        return Api.games.startNewGame()
    }

}