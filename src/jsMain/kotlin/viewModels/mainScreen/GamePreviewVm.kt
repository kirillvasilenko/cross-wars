package viewModels.mainScreen

import Api
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mainScope
import model.*
import viewModels.*
import kotlin.js.Date
import kotlin.math.roundToLong
import kotlin.random.Random

class LastMoveTimeVm(lastMoveDate: Long): ViewModel(){

    private var lastMoveDate = lastMoveDate

    private var updateTimeJob: Job? = null

    val lastMoveWasTimeAgo: String
        get(){
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
                raiseChanged()
            }
        }
    }

    override suspend fun dispose() {
        if(updateTimeJob != null){
            updateTimeJob!!.cancel()
        }
    }

    fun setLastMoveDate(moveDate: Long){
        lastMoveDate = moveDate
    }

}

class GamePreviewVm(game: GameDto): CommandVm<GameDto>() {

    val gameId: Int = game.id

    var users: MutableList<UserInGame> = game.users

    var state = game.state

    val activeUsersCount: Int
        get() = users.filter { it.active }.size

    val visible: Boolean
        get() = state == GameState.ACTIVE


    val lastMoveTimeVm = LastMoveTimeVm(game.lastMovedDate)


    override suspend fun initImpl() {
        log("gameVm $gameId: init")
        SubscriptionHub.subscribeOnGameEvents(
                gameId,
                GameEventHandler(::handleGameEvent)
        )
    }

    override suspend fun dispose() {
        SubscriptionHub.unsubscribeFromGameEvents(gameId)
    }

    private suspend fun handleGameEvent(event: GameEvent){
        when(event){
            is GameStateChanged -> onGameStateChanged(event)
            is UserJoined -> onUserJoined(event)
            is UserLeaved -> onUserLeaved(event)
            is UserMoved -> onUserMoved(event)
            is UserSubscribedOnGameEvents -> onUserSubscribedOnGameEvents(event)
            else -> Unit // ignore
        }
    }

    private fun onGameStateChanged(event: GameStateChanged){
        if(state == event.actualState) return
        log(event.toString())
        state = event.actualState
        raiseChanged()
    }

    private fun onUserJoined(event: UserJoined){
        val user = users.firstOrNull { it.id == event.user.id }
        if(user != null){
            if(user.active) return
            user.active = true
        }
        else{
            users.add(event.user)
        }
        raiseChanged()
    }

    private fun onUserLeaved(event: UserLeaved){
        var user = users.firstOrNull { it.id == event.user.id }
        if (user == null){
            user = event.user
            users.add(event.user)
        }

        if(!user.active) return

        user.active = false
        raiseChanged()
    }

    private fun onUserMoved(event: UserMoved){
        lastMoveTimeVm.setLastMoveDate(Date.now().roundToLong())
    }

    private fun onUserSubscribedOnGameEvents(event: UserSubscribedOnGameEvents){
        resetAll(event.game)
        raiseChanged()
    }

    private fun resetAll(game:GameDto){
        users = game.users
        state = game.state
        lastMoveTimeVm.setLastMoveDate(game.lastMovedDate)
    }

    override suspend fun executeImpl(): GameDto {
        return Api.games.joinGame(gameId)
    }

}