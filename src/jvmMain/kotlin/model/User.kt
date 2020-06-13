package model

import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class User(val id: Int, val name: String, val sideOfTheForce: SideOfTheForce, val swordColor: Int){

    private val mutex: Mutex =
        Mutex()

    private var currentGame: Game? = null

    private var wsConnection: WsConnection? = null

    suspend fun startNewGame(): Game {
        mutex.withLock{
            checkCurrentGameIsNull("User must leave current game before starting new one.")
            val game = GamesStorage.makeGame()
            game.eventsListener = SubscriptionsHub::handleGameEvent
            currentGame = game
            game.start(this)
            return game
        }
    }

    suspend fun joinGame(gameId: Int): Game {
        mutex.withLock{
            if(currentGame?.id == gameId) return currentGame!!
            checkCurrentGameIsNull("User must leave current game before join another one.")

            val game = GamesStorage.getGame(gameId)
            game.join(this)
            return game
        }
    }

    suspend fun leaveCurrentGame(){
        mutex.withLock{
            if(currentGame == null) return
            currentGame!!.leave(this)
            currentGame = null
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
        mutex.withLock{
            if(wsConnection != null) userFault("Trying open more than one ws connection. Only one ws connection is available for user.")
            val connection = SubscriptionsHub.createConnection(id, incoming, outgoing)
            wsConnection = connection
        }
        wsConnection!!.runSendingEvents()
        wsConnection!!.listen()
        mutex.withLock{
            unsubscribeFromCommonEventsImpl()
            unsubscribeFromCurrentGameEventsImpl()
            SubscriptionsHub.deleteConnection(id)
            wsConnection = null
        }
    }

    suspend fun subscribeOnCommonEvents() {
        mutex.withLock {
            if(wsConnection == null) userFault(
                "Trying to subscribe without set ws connection. Set wsConnection and try again."
            )
            SubscriptionsHub.subscribeOnCommonEvents(id)
            for(game in GamesStorage.getGames()){
                game.onSubscribed(id)
            }
        }
    }

    suspend fun unsubscribeFromCommonEvents() {
        mutex.withLock {
            unsubscribeFromCommonEventsImpl()
        }
    }

    suspend fun subscribeOnCurrentGameEvents() {
        mutex.withLock {
            if(currentGame == null) userFault(
                "Trying to subscribe on current game without current game. Join to some game and try again."
            )
            if(wsConnection == null) userFault(
                "Trying to subscribe without set ws connection. Set wsConnection and try again."
            )
            currentGame!!.subscribe(this)
        }
    }

    suspend fun unsubscribeFromCurrentGameEvents() {
        mutex.withLock {
            if(wsConnection == null) return
            unsubscribeFromCommonEventsImpl()
        }
    }

    suspend fun snapshot(): UserDto{
        mutex.withLock{
            return UserDto(id, name, currentGame?.id, sideOfTheForce, swordColor)
        }
    }

    private fun unsubscribeFromCommonEventsImpl(){
        SubscriptionsHub.unsubscribeFromCommonEvents(id)
    }

    private suspend  fun unsubscribeFromCurrentGameEventsImpl(){
        if(currentGame == null) return
        currentGame!!.unsubscribe(this)
    }

    private fun checkCurrentGameIsNull(errorMessage: String){
        if(currentGame != null) userFault(errorMessage)
    }

}