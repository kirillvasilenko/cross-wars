package com.vkir.model

import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class User(
    val id: Int,
    val name: String,
    val sideOfTheForce: SideOfTheForce,
    val swordColor: Int){

    private val mutex: Mutex =
        Mutex()

    var currentGame: Game? = null
        private set

    var eventsListener: suspend (UserEvent) -> Unit = {}

    suspend fun startNewGame(): Game {
        mutex.withLock{
            checkCurrentGameIsNull("User must leave current game before starting new one.")
            val game = GamesStorage.makeGame()
            currentGame = game
            game.start(this)
            raiseEvent(UserJoinedGame(id, game.id))
            return game
        }
    }

    suspend fun joinGame(gameId: Int): Game {
        mutex.withLock{
            if(currentGame?.id == gameId) return currentGame!!
            checkCurrentGameIsNull("User must leave current game before join another one.")


            val game = GamesStorage.getGame(gameId)
            game.join(this)
            currentGame = game
            raiseEvent(UserJoinedGame(id, game.id))
            return game
        }
    }

    suspend fun leaveCurrentGame(){
        mutex.withLock{
            if(currentGame == null) return
            val gameId = currentGame!!.id
            currentGame!!.leave(this)
            currentGame = null
            raiseEvent(UserLeavedGame(id, gameId))
        }
    }

    suspend fun makeMove(x: Int, y: Int){
        mutex.withLock{
            if(currentGame == null) userFault(
                "User must join a game for making move."
            )
            currentGame!!.makeMove(this, x, y)
        }
    }

    suspend fun connect(incoming: ReceiveChannel<Frame>, outgoing: SendChannel<Frame>) {
        val connection = SubscriptionsHub.createConnection(id, incoming, outgoing)
        connection.listenAndSend()
    }

    suspend fun snapshot(): UserDto {
        mutex.withLock{
            return UserDto(id, name, currentGame?.id, sideOfTheForce, swordColor)
        }
    }

    private fun checkCurrentGameIsNull(errorMessage: String){
        if(currentGame != null) userFault(errorMessage)
    }

    private suspend fun raiseEvent(event: UserEvent){
        eventsListener(event)
    }

}