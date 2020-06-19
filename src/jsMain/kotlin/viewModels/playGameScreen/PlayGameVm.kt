package viewModels.playGameScreen

import api.Api
import model.*
import viewModels.GameEventHandler
import viewModels.SubscriptionHub
import viewModels.common.ViewModel
import viewModels.common.VmEvent

class UserLeavedCurrentGame(source: ViewModel): VmEvent(source)

class PlayGameVm(val currentUser: UserDto, val gameId: Int): ViewModel(){

    val leaveGameVm = child(LeaveGameVm())

    private val users = mutableListOf<UserInGameVm>()

    val resultVm = GameResultsVm()

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
        if(event is UserSubscribedOnGameEvents){
            resetAll(event.game)
            return
        }
        if(!this::gameBoardVm.isInitialized) return

        when(event){
            is GameStateChanged -> gameBoardVm.state = event.actualState
            is UserJoined -> setUserActivity(event.user, true)
            is UserLeaved -> handleUserLeaved(event)
            is UserMoved -> gameBoardVm.userMoved(event)
            is UserWon -> handleUserWon(event)
            is Draw -> handleDraw()
            else -> Unit // ignore
        }
    }

    private fun handleUserWon(event: UserWon){
        gameBoardVm.userWon(event)
        users.firstOrNull { it.userId == event.userId }?.let{
            resultVm.userWin(it)
        }
    }

    private fun handleDraw(){
        gameBoardVm.draw()
        resultVm.draw()
    }

    private suspend fun resetAll(game:GameDto){
        users.forEach { removeChild(it) }
        users.clear()
        game.users.forEach { addUserVm(it) }

        if(this::legendVm.isInitialized) removeChild(legendVm)
        legendVm = child(LegendVm(currentUser, users))

        if(this::gameBoardVm.isInitialized) removeChild(gameBoardVm)
        gameBoardVm = child(GameBoardVm(currentUser, users, game))

        raiseStateChanged()
    }

    private suspend fun handleUserLeaved(event: UserLeaved){
        if(event.user.id == currentUser.id)
            raiseEvent(UserLeavedCurrentGame(this))
        setUserActivity(event.user, false)
    }

    private suspend fun setUserActivity(userInGame: UserInGame, active: Boolean) {
        val user = users.firstOrNull { it.userId == userInGame.id }
        if (user == null) {
            addUserVm(userInGame)
            legendVm.usersChanged()
        } else {
            user.active = active
        }
    }

    private suspend fun addUserVm(userInGame: UserInGame) {
        val userDto = Api.users.getUser(userInGame.id)
        users.add(child(UserInGameVm(userDto, userInGame)))
    }

}

