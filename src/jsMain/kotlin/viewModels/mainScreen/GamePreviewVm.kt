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

class JoinedGame(source: ViewModel, val game:GameDto): VmEvent(source)

class GamePreviewVm(game: GameDto): CommandVm() {

    val gameId: Int = game.id

    var users: MutableList<UserInGame> = game.users

    var state = game.state

    val activeUsersCount: Int
        get() = users.filter { it.active }.size

    val visible: Boolean
        get() = state == GameState.ACTIVE

    /**
     * Count fields that are occupied.
     * */
    var boardFilled: Int = game.board.flatten().filterNotNull().size
        private set

    val lastMoveTimeVm = child(LastMoveTimeVm(game.lastMovedDate))


    override suspend fun initImpl() {
        SubscriptionHub.subscribeOnGameEvents(
                gameId,
                GameEventHandler(::handleGameEvent)
        )
    }

    override suspend fun disposeImpl() {
        SubscriptionHub.unsubscribeFromGameEvents(gameId)
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
        log(event.toString())
        state = event.actualState
        raiseStateChanged()
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
        raiseStateChanged()
    }

    private fun onUserLeaved(event: UserLeaved){
        var user = users.firstOrNull { it.id == event.user.id }
        if (user == null){
            user = event.user
            users.add(event.user)
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

    private fun resetAll(game:GameDto){
        users = game.users
        state = game.state
        lastMoveTimeVm.setLastMoveDate(game.lastMovedDate)
        boardFilled = game.board.flatten().filterNotNull().size
        raiseStateChanged()
    }

    override suspend fun executeImpl(): VmEvent {
        val game = Api.games.joinGame(gameId)
        return JoinedGame(this, game)
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