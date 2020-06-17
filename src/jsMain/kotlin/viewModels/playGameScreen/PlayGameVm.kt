package viewModels.playGameScreen

import Api
import model.*
import viewModels.GameEventHandler
import viewModels.SubscriptionHub
import viewModels.common.ViewModel

class PlayGameVm(val currentUser: UserDto, val gameId: Int): ViewModel(){

    val leaveGameVm = child(LeaveGameVm())

    private val users = mutableListOf<UserInGameVm>()

    lateinit var legendVm: LegendVm
        private set

    lateinit var gameBoardVm: GameBoardVm
        private set


    override suspend fun initImpl() {
        subscribe()
    }

    override suspend fun disposeImpl() {
        SubscriptionHub.unsubscribeFromGameEvents(gameId)
    }

    private suspend fun subscribe(){
        SubscriptionHub.subscribeOnGameEvents(
                gameId,
                GameEventHandler(::handleGameEvent, ::subscribe)
        )
    }

    private suspend fun handleGameEvent(event: GameEvent){
        when(event){
            is UserSubscribedOnGameEvents -> resetAll(event.game)
            is GameStateChanged -> gameBoardVm.state = event.actualState
            is UserJoined -> setUserActivity(event.user, true)
            is UserLeaved -> setUserActivity(event.user, false)
            is UserMoved -> gameBoardVm.userMoved(event)
            is UserWon -> Unit
            is Draw -> Unit
            else -> Unit // ignore
        }
    }

    private suspend fun resetAll(game:GameDto){
        users.forEach { removeChild(it) }
        users.clear()
        game.users.forEach { addUserVm(it) }

        if(this::legendVm.isInitialized) removeChild(legendVm)
        legendVm = child(LegendVm(currentUser, users))

        if(this::gameBoardVm.isInitialized) removeChild(gameBoardVm)
        gameBoardVm = child(GameBoardVm(currentUser, users, game.state, game.board))

        raiseStateChanged()
    }

    private suspend fun setUserActivity(userInGame: UserInGame, active: Boolean) {
        val user = users.firstOrNull { it.userId == userInGame.id }
        if (user == null) {
            addUserVm(userInGame)
            legendVm.usersChanged()
        } else {
            user.active = userInGame.active
        }
    }

    private suspend fun addUserVm(userInGame: UserInGame) {
        val userDto = Api.users.getUser(userInGame.id)
        var userVm = child(UserInGameVm(userDto, userInGame))
        users.add(userVm)
    }

}

