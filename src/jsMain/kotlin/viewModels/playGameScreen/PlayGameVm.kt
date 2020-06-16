package viewModels.playGameScreen

import Api
import model.*
import viewModels.GameEventHandler
import viewModels.SubscriptionHub
import viewModels.ViewModel

class PlayGameVm(val currentUser: UserDto, val gameId: Int): ViewModel(){

    var onLeaveGame: suspend () -> Unit = {}


    val leaveGameVm = LeaveGameVm()

    lateinit var users: MutableList<UserInGameVm>
        private set

    lateinit var legendVm: LegendVm
        private set

    lateinit var gameBoardVm: GameBoardVm
        private set


    init{
        leaveGameVm.onExecuted = {
            onLeaveGame()
        }
    }

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
        users = game.users
                .map { userInGame ->
                    val userDto = Api.users.getUser(userInGame.id)
                    UserInGameVm(userDto, userInGame)
                }.toMutableList()
        legendVm = LegendVm(currentUser, users)
        gameBoardVm = GameBoardVm(currentUser, users, game.state, game.board)

        raiseChanged()
    }

    private suspend fun setUserActivity(userInGame: UserInGame, active: Boolean) {
        val user = users.firstOrNull { it.userId == userInGame.id }
        if (user == null) {
            addUserVm(userInGame)
        } else {
            user.active = userInGame.active
        }
    }

    private suspend fun addUserVm(userInGame: UserInGame): UserInGameVm {
        val userDto = Api.users.getUser(userInGame.id)
        var userVm = UserInGameVm(userDto, userInGame)
        users.add(userVm)

        legendVm.usersChanged()
        return userVm
    }

}

