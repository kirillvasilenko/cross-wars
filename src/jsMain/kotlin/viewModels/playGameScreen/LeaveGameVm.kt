package viewModels.playGameScreen

import Api
import viewModels.common.CommandVm
import viewModels.common.ViewModel
import viewModels.common.VmEvent

class LeaveGameVm: CommandVm(){
    override suspend fun executeImpl() {
        Api.games.leaveCurrentGame()
    }
}