package viewModels.playGameScreen

import Api
import viewModels.CommandVm

class LeaveGameVm: CommandVm<Unit>(){
    override suspend fun executeImpl() {
        Api.games.leaveCurrentGame()
    }
}