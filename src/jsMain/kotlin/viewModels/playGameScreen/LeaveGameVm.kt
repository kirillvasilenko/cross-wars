package viewModels.playGameScreen

import Api
import viewModels.common.CommandVm
import viewModels.common.ViewModel
import viewModels.common.VmEvent

class LeavedGame(source: ViewModel): VmEvent(source)

class LeaveGameVm: CommandVm(){
    override suspend fun executeImpl(): VmEvent {
        Api.games.leaveCurrentGame()
        return LeavedGame(this)
    }
}