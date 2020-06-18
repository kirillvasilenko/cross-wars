package viewModels.playGameScreen

import model.*
import viewModels.common.CommandVm
import viewModels.common.ViewModel

class GameBoardVm(
        val currentUser: UserDto,
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
                    BoardFieldVm(x, y, null, active)
                else{
                    val user = users.first { it.userId == userInGame.id }
                    BoardFieldVm(
                            x, y,
                            UserInGameSymbolVm(
                                    user.userSymbol,
                                    user.sideOfTheForce,
                                    user.swordColor),
                            active)
                }
            }.toMutableList()
        }
    }

    fun userMoved(ev: UserMoved){
        setFieldsActive(ev.userId != currentUser.id)

        val field = board[ev.x][ev.y]
        val user = users.first { it.userId == ev.userId }
        field.currentState = UserInGameSymbolVm(
                user.userSymbol,
                user.sideOfTheForce,
                user.swordColor)
    }

    fun userWon(ev: UserWon){
        hideAll()
        ev.winLine.forEach {
            board[it.x][it.y].show()
        }
    }

    fun draw(){
        hideAll()
    }

    private fun hideAll(){
        board.forEach { row ->
            row.forEach { field ->
                field.hide()
            }
        }
    }

    private fun setFieldsActive(active:Boolean){
        board.forEach { row ->
            row.forEach { field ->
                field.active = active
            }
        }
    }
}

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

    fun hide(){
        if(currentState != null){
            currentState!!.aLittleHidden = true
            currentState!!.glowable = false
            raiseStateChanged()
        }
    }

    fun show(){
        if(currentState != null){
            currentState!!.aLittleHidden = false
            currentState!!.glowable = true
            raiseStateChanged()
        }
    }

    override val canExecuted: Boolean
        get() = super.canExecuted && currentState == null && active

    override suspend fun executeImpl() {
        Api.games.makeMove(x, y)
    }

}
