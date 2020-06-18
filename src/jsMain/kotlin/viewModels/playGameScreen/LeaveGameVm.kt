package viewModels.playGameScreen

import api.Api
import viewModels.common.CommandVm

class LeaveGameVm: CommandVm(){
    override suspend fun executeImpl() {
        Api.games.leaveCurrentGame()
    }
}