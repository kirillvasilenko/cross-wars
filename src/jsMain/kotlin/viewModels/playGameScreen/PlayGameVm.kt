package viewModels.playGameScreen

import Api
import model.GameDto
import viewModels.CommandVm
import viewModels.ViewModel

class LeaveGameVm: CommandVm<Unit>(){
    override suspend fun executeImpl() {
        Api.games.leaveCurrentGame()
    }
}

class PlayGameVm(game: GameDto): ViewModel(){

    var onLeaveGame: () -> Unit = {}

    val legendVm = LegendVm(game.users)

    val leaveGameVm = LeaveGameVm()

    init{
        leaveGameVm.onExecuted = {
            onLeaveGame()
        }
    }

}

