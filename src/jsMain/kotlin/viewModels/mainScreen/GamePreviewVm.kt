package viewModels.mainScreen

import viewModels.GameEventHandler
import viewModels.SubscriptionHub
import api.Api
import kotlinx.coroutines.*
import log
import mainScope
import model.*
import viewModels.common.CommandVm
import viewModels.common.ViewModel
import viewModels.common.VmEvent
import viewModels.playGameScreen.UserInGameVm
import kotlin.js.Date
import kotlin.math.roundToLong

class UserJoinedGame(source: ViewModel, val gameId:Int): VmEvent(source)

class GamePreviewVm(private val currentUserId: Int, private var game: GameDto): CommandVm() {

    private var state: GameState = game.state

    private val users = mutableListOf<UserInGameVm>()


    val gameId: Int
        get() = game.id

    val activeUsers: List<UserInGameVm>
        get() = users.filter{ it.active }

    val visible: Boolean
        get() = state == GameState.ACTIVE

    /**
     * Count fields that are occupied.
     * */
    var boardFilled: Int = game.board.flatten().filterNotNull().size
        private set

    val lastMoveTimeVm = child(LastMoveTimeVm(game.lastMovedDate))


    override suspend fun initImpl() {
        resetUsers()
        SubscriptionHub.subscribeOnGameEvents(
                game.id,
                GameEventHandler(::handleGameEvent)
        )
    }

    override suspend fun disposeImpl() {
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
            is UserSubscribedOnGameEvents -> resetAll(event.game)
            else -> Unit // ignore
        }
    }

    private fun onGameStateChanged(event: GameStateChanged){
        if(state == event.actualState) return
        state = event.actualState
        raiseStateChanged()
    }

    private suspend fun onUserJoined(event: UserJoined){
        if(event.user.id == currentUserId)
            raiseEvent(UserJoinedGame(this, gameId))

        val user = users.firstOrNull { it.userId == event.user.id }
        if(user != null){
            if(user.active) return
            user.active = true
        }
        else{
            users.add(makeUserVm(event.user))
        }
        raiseStateChanged()
    }

    private suspend fun onUserLeaved(event: UserLeaved){
        var user = users.firstOrNull { it.userId == event.user.id }
        if (user == null){
            user = makeUserVm(event.user)
            users.add(user)
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

    private suspend fun resetAll(actualGame:GameDto){
        game = actualGame
        state = game.state
        resetUsers()
        lastMoveTimeVm.setLastMoveDate(game.lastMovedDate)
        boardFilled = game.board.flatten().filterNotNull().size
        raiseStateChanged()
    }

    private suspend fun resetUsers(){
        users.clear()
        val defs = game.users.map{ userInGame ->
            mainScope.async { makeUserVm(userInGame) }
        }
        users.addAll(defs.awaitAll())
    }

    private suspend fun makeUserVm(userInGame:UserInGame): UserInGameVm{
        val userDto = Api.users.getUser(userInGame.id)
        return UserInGameVm(userDto, userInGame)
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