package com.vkir.viewModels.playGameScreen

import com.vkir.model.*
import com.vkir.viewModels.BackendEventsHandler
import com.vkir.viewModels.SubscriptionHub
import com.vkir.viewModels.common.ViewModel

class PlayGameVm(
    private val currentUser: UserDto,
    private val gameId: Int
) : ViewModel() {

    val leaveGameVm = LeaveGameVm()

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

    private suspend fun subscribe() {
        SubscriptionHub.subscribeOnGameEvents(
            gameId,
            BackendEventsHandler(::handleGameEvent, ::subscribe)
        )
    }

    private suspend fun handleGameEvent(event: GameEvent) {
        if (event is GameSnapshot) {
            resetAll(event.game)
            return
        }
        if (!this::gameBoardVm.isInitialized) return

        when (event) {
            is GameStateChanged -> gameBoardVm.state = event.actualState
            is UserJoined -> setUserActivity(event.user, true)
            is UserLeaved -> handleUserLeaved(event)
            is UserMoved -> gameBoardVm.userMoved(event)
            is UserWon -> handleUserWon(event)
            is Draw -> handleDraw()
            else -> Unit // ignore
        }
    }

    private fun handleUserWon(event: UserWon) {
        gameBoardVm.userWon(event)
        users.firstOrNull { it.userId == event.userId }?.let {
            resultVm.userWin(it)
        }
    }

    private fun handleDraw() {
        gameBoardVm.draw()
        resultVm.draw()
    }

    private fun resetAll(game: GameDto) {
        users.clear()
        game.users.forEach { addUserVm(it) }

        legendVm = LegendVm(users)
        gameBoardVm = GameBoardVm(currentUser, users, game)

        raiseStateChanged()
    }

    private fun handleUserLeaved(event: UserLeaved) {
        setUserActivity(event.user, false)
    }

    private fun setUserActivity(userInGame: UserInGame, active: Boolean) {
        val user = users.firstOrNull { it.userId == userInGame.id }
        if (user == null) {
            addUserVm(userInGame)
            legendVm.usersChanged()
        } else {
            user.active = active
        }
    }

    private fun addUserVm(userInGame: UserInGame) {
        users.add(UserInGameVm(userInGame))
    }

}

