package viewModels.playGameScreen

import Api
import viewModels.CommandVm
import viewModels.ViewModel
import viewModels.VmEvent

class LeavedGame(source: ViewModel): VmEvent(source)

class LeaveGameVm: CommandVm(){
    override suspend fun executeImpl(): VmEvent {
        Api.games.leaveCurrentGame()
        return LeavedGame(this)
    }
}