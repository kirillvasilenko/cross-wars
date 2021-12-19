package com.vkir.model

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

import com.vkir.model.GameState.*

class Game(val id: Int){

    private val mutex = Mutex()

    var state: GameState = CREATED
        private set

    private val users = mutableListOf<UserInGame>()

    private var winningUser: UserInGame? = null

    private val board = GameBoard(users)

    private var lastUsedSymbol = -1

    val createdTime: Long = nowUtcMills()

    var eventsListener: suspend (GameEvent) -> Unit = {}

    //region public

    suspend fun start(user: User){
        mutex.withLock {
            if(state != CREATED) userFault(
                "Trying to start game that is in $state state. Only game is in $CREATED state can be started."
            )
            state = ACTIVE
            eventsListener = SubscriptionsHub::handleGameEvent
            joinImpl(user)
            raiseEvent(GameStateChanged(id, ACTIVE))
            raiseEvent(GameStarted(id, user.id))
        }
    }

    suspend fun join(user: User){
        mutex.withLock {
            when(state){
                ACTIVE -> joinImpl(user)
                CREATED -> userFault(
                    "Trying to join to a inactive game. Join to an active game."
                )
                COMPLETED -> userFault(
                    "Trying to join to a completed game. Join to an active game."
                )
                ARCHIVED -> userFault(
                    "Trying to join to a archived game. Join to an active game."
                )
            }
        }
    }

    suspend fun leave(user: User){
        mutex.withLock{
            leaveImpl(user)
        }
    }

    suspend fun makeMove(user: User, x: Int, y: Int) {
        mutex.withLock{
            checkIfCanMove(user, x, y)
            makeMoveImpl(user, x, y)
        }
    }

    suspend fun sendSnapshot(connection:WsConnection){
        mutex.withLock {
            connection.send(GameSnapshot(id, snapshotImpl()))
        }
    }

    suspend fun snapshot(): GameDto {
        mutex.withLock {
            return snapshotImpl()
        }
    }

    //endregion public

    //region private

    private suspend fun makeMoveImpl(user: User, x: Int, y: Int){
        if(board.isUserAlreadyOccupiedField(user.id,x,y)) return

        // make move
        val userInGame = users.first { it.id == user.id }
        board.makeMove(x, y, userInGame.id)
        raiseEvent(UserMoved(id, user.id, board.lastMovedTime, x, y))

        // may be win?
        val winLine = board.findWinLine(userInGame.id, x, y)
        // if not win and not draw - continue game
        if(winLine == null && !board.isDraw()) return

        // win
        if(winLine != null){
            winningUser = userInGame
            raiseEvent(UserWon(id, user.id, winLine))
        }
        // draw
        else{
            raiseEvent(Draw(id))
        }

        // and complete game
        state = COMPLETED
        raiseEvent(GameStateChanged(id, COMPLETED))
    }


    private fun checkIfCanMove(user: User, x: Int, y: Int){
        if(!isUserInGame(user.id)) userFault("Join to game $id for making move.")
        if(state != ACTIVE) userFault(
            "Trying to make move in $state game."
        )
        board.checkIfCanMove(user.id, x, y)
    }


    private suspend fun joinImpl(user: User){
        if(isUserInGame(user.id)) return

        var userInGame = users.firstOrNull{ it.id == user.id }

        if(userInGame == null){
            lastUsedSymbol++
            userInGame = UserInGame(
                user.id,
                lastUsedSymbol,
                true,
                user.name,
                user.sideOfTheForce,
                user.swordColor
            )
            users.add(userInGame)
        }
        else{
            userInGame.active = true
        }

        raiseEvent(UserJoined(id, userInGame.copy()))
    }

    private suspend fun leaveImpl(user: User){
        if(!isUserInGame(user.id)) return

        val userInGame = users.first{ it.id == user.id }
        userInGame.active = false
        raiseEvent(UserLeaved(id, userInGame.copy()))

        if(!users.any{ it.active }){
            archive()
        }
    }

    private fun isUserInGame(userId: Int) = users.any { it.id == userId && it.active }

    private suspend fun archive(){
        state = ARCHIVED
        raiseEvent(GameStateChanged(id, ARCHIVED))
        eventsListener = {}
    }

    private fun snapshotImpl() =
            GameDto(id,
                    state,
                    board.lastMovedTime,
                    board.lastMovedUserId,
                    users.toMutableList(),
                    board.snapshot())

    private suspend fun raiseEvent(event: GameEvent){
        eventsListener(event)
    }

    //endregion private

}