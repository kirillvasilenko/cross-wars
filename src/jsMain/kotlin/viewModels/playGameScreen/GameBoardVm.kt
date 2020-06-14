package viewModels.playGameScreen

import model.UserDto
import model.UserInGame
import viewModels.CommandVm
import viewModels.ViewModel

class GameBoardVm(
        val user: UserDto,
        val users: MutableList<UserInGameVm>,
        board: List<MutableList<UserInGame?>>)
    : ViewModel() {

    val board: List<MutableList<BoardFieldVm>>

    init {
        this.board = board.mapIndexed { x, row ->
            row.mapIndexed { y, userInGame ->
                if (userInGame == null)
                    return@mapIndexed BoardFieldVm(x, y, null)

                val user = users.first { it.userId == userInGame.id }
                BoardFieldVm(x, y,
                        UserInGameSymbolVm(
                                user.userSymbol,
                                user.sideOfTheForce,
                                user.swordColor)
                )
            }.toMutableList()
        }
    }
}

class BoardFieldVm(val x: Int, val y: Int, currentState: UserInGameSymbolVm?): CommandVm<Unit>(){

    var currentState: UserInGameSymbolVm? = currentState
        private set

    override suspend fun executeImpl() {
        if(currentState != null) return
        Api.games.makeMove(x, y)
    }

}
