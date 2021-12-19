package com.vkir.viewModels.mainScreen

import com.vkir.api.Api
import com.vkir.model.*
import com.vkir.viewModels.common.CommandVm
import com.vkir.viewModels.common.ViewModel
import kotlinx.coroutines.*
import com.vkir.mainScope
import com.vkir.viewModels.BackendEventsHandler
import com.vkir.viewModels.SubscriptionHub
import kotlin.js.Date
import kotlin.math.roundToLong


class GamePreviewVm(private var game: GameDto): CommandVm() {

    val gameId: Int
        get() = game.id

    val activeUsers: List<UserInGame>
        get() = game.users.filter{ it.active }

    val visible: Boolean
        get() = game.state == GameState.ACTIVE

    /**
     * Count fields that are occupied.
     * */
    var boardFilled: Int = game.board.flatten().filterNotNull().size
        private set

    val lastMoveTimeVm = LastMoveTimeVm(game.lastMovedDate)


    override suspend fun initImpl() {
        SubscriptionHub.subscribeOnGameEvents(
                game.id,
                BackendEventsHandler(::handleGameEvent)
        )
    }

    override suspend fun disposeImpl() {
        lastMoveTimeVm.dispose()
        SubscriptionHub.unsubscribeFromGameEvents(game.id)
    }

    override suspend fun executeImpl() {
        Api.games.joinGame(game.id)
    }


    private suspend fun handleGameEvent(event: GameEvent){
        when(event){
            is GameStateChanged -> onGameStateChanged(event)
            is UserJoined -> onUserJoined(event)
            is UserLeaved -> onUserLeaved(event)
            is UserMoved -> onUserMoved()
            is GameSnapshot -> resetAll(event.game)
            else -> Unit // ignore
        }
    }

    private fun onGameStateChanged(event: GameStateChanged){
        if(game.state == event.actualState) return
        game.state = event.actualState
        raiseStateChanged()
    }

    private fun onUserJoined(event: UserJoined){
        val user = game.users.firstOrNull { it.id == event.user.id }
        if(user != null){
            if(user.active) return
            user.active = true
        }
        else{
            game.users.add(event.user)
        }
        raiseStateChanged()
    }

    private fun onUserLeaved(event: UserLeaved){
        var user = game.users.firstOrNull { it.id == event.user.id }
        if (user == null){
            game.users.add(event.user)
            user = event.user
        }

        if(!user.active) return

        user.active = false
        raiseStateChanged()
    }

    private fun onUserMoved() {
        lastMoveTimeVm.setLastMoveDate(Date.now().roundToLong())
        boardFilled++
        raiseStateChanged()
    }

    private fun resetAll(actualGame:GameDto){
        game = actualGame
        lastMoveTimeVm.setLastMoveDate(game.lastMovedDate)
        boardFilled = game.board.flatten().filterNotNull().size
        raiseStateChanged()
    }

}

class LastMoveTimeVm(private var lastMoveDate: Long): ViewModel(){

    private var updateTimeJob: Job? = null

    val lastMoveWasTimeAgo: String
        get(){
            if(lastMoveDate == 0L) return "--"

            val ms = Date.now().roundToLong() - lastMoveDate
            val sec = ms / 1000
            if(sec < 60){
                return "$sec sec."
            }
            val min = sec / 60
            return "$min min."
        }

    override suspend fun initImpl() {
        updateTimeJob = mainScope.launch{
            while(true){
                delay(1000)
                raiseStateChanged()
            }
        }
    }

    override suspend fun disposeImpl() {
        if(updateTimeJob != null){
            updateTimeJob!!.cancel()
        }
    }

    fun setLastMoveDate(moveDate: Long){
        lastMoveDate = moveDate
    }

}