package viewModels.playGameScreen

import model.GameState
import model.UserDto
import model.UserInGame
import model.UserMoved
import viewModels.common.CommandVm
import viewModels.common.ViewModel
import viewModels.common.VmEvent

class GameBoardVm(
        val user: UserDto,
        val users: MutableList<UserInGameVm>,
        state: GameState,
        board: List<MutableList<UserInGame?>>)
    : ViewModel() {

    val board: List<MutableList<BoardFieldVm>>

    var state:GameState = state
        set(value){
            if(field == value) return
            field = value
            setFieldsActive(active)
        }

    private val active:Boolean
        get() = state == GameState.ACTIVE

    init {
        this.board = board.mapIndexed { x, row ->
            row.mapIndexed { y, userInGame ->
                if (userInGame == null)
                    return@mapIndexed BoardFieldVm(x, y, null, active)

                val user = users.first { it.userId == userInGame.id }
                BoardFieldVm(
                        x, y,
                        child(UserInGameSymbolVm(
                                user.userSymbol,
                                user.sideOfTheForce,
                                user.swordColor)),
                        active
                )
            }.toMutableList()
        }
    }

    fun userMoved(ev: UserMoved){
        val field = board[ev.x][ev.y]
        val user = users.first { it.userId == ev.userId }
        field.currentState = UserInGameSymbolVm(
                user.userSymbol,
                user.sideOfTheForce,
                user.swordColor)
    }

    private fun setFieldsActive(active:Boolean){
        board.forEach { row ->
            row.forEach { field ->
                field.active = active
            }
        }
    }
}

class MoveMade(source: ViewModel, val x: Int, val y: Int): VmEvent(source)

class BoardFieldVm(val x: Int, val y: Int, currentState: UserInGameSymbolVm?, active: Boolean = true): CommandVm(){

    var currentState: UserInGameSymbolVm? = currentState
        set(value){
            field = value
            raiseStateChanged()
        }

    var active: Boolean = active
        set(value){
            if(field == value) return
            field = value
            raiseStateChanged()
        }

    override val canExecuted: Boolean
        get() = super.canExecuted && currentState == null && active

    override suspend fun executeImpl(): VmEvent {
        Api.games.makeMove(x, y)
        return MoveMade(this, x, y)
    }

}
