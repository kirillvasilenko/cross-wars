package com.vkir.viewModels.playGameScreen

import com.vkir.api.Api
import com.vkir.model.*
import com.vkir.viewModels.common.CommandVm
import com.vkir.viewModels.common.ViewModel

class GameBoardVm(
    val currentUser: UserDto,
    val users: MutableList<UserInGameVm>,
    game: GameDto
) : ViewModel() {

    val board: List<MutableList<BoardFieldVm>>

    var state: GameState = game.state
        set(value){
            if(field == value) return
            field = value
            setFieldsActive(active)
        }

    private val active:Boolean
        get() = state == GameState.ACTIVE

    init {
        this.board = game.board.mapIndexed { x, row ->
            row.mapIndexed { y, userIdOnField ->
                if (userIdOnField == null)
                    BoardFieldVm(x, y, null, active)
                else{
                    val user = users.first { it.userId == userIdOnField }
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
        if(currentUser.id == game.lastMovedUserId){
            setFieldsActive(false)
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

    override suspend fun executeImpl() {
        Api.games.makeMove(x, y)
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


}
