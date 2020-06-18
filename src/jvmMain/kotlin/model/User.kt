package model

import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.coroutineContext

class User(
        val id: Int,
        val name: String,
        val sideOfTheForce: SideOfTheForce,
        val swordColor: Int){

    private val mutex: Mutex =
        Mutex()

    var currentGame: Game? = null
        private set

    private var wsConnection: WsConnection? = null

    private val subscribedOnGames = mutableSetOf<Int>()

    suspend fun startNewGame(): Game {
        mutex.withLock{
            checkCurrentGameIsNull("User must leave current game before starting new one.")
            val game = GamesStorage.makeGame()
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
            currentGame = game
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
        wsConnection!!.runSendingEvents(CoroutineScope(coroutineContext))
        wsConnection!!.listen()
        mutex.withLock{
            unsubscribeFromGameStartedImpl()
            unsubscribeFromAllGames()
            SubscriptionsHub.deleteConnection(id)
            wsConnection = null
        }
    }

    suspend fun subscribeOnGameStartedEvents() {
        mutex.withLock {
            if(wsConnection == null) userFault(
                "Trying to subscribe without set ws connection. Set wsConnection and try again."
            )
            SubscriptionsHub.subscribeOnGameStartedEvents(id)
        }
    }

    suspend fun unsubscribeFromGameStartedEvents() {
        mutex.withLock {
            unsubscribeFromGameStartedImpl()
        }
    }

    suspend fun subscribeOnGameEvents(gameId: Int) {
        mutex.withLock {
            if(wsConnection == null) userFault(
                "Trying to subscribe without set ws connection. Set wsConnection and try again."
            )
            val game = GamesStorage.getGame(gameId)
            game.subscribe(this)
            subscribedOnGames.add(gameId)
        }
    }

    suspend fun unsubscribeFromGameEvents(gameId: Int) {
        mutex.withLock {
            if(wsConnection == null) return
            unsubscribeFromGameEventsImpl(gameId)
        }
    }

    suspend fun snapshot(): UserDto{
        mutex.withLock{
            return UserDto(id, name, currentGame?.id, sideOfTheForce, swordColor)
        }
    }

    private fun unsubscribeFromGameStartedImpl(){
        SubscriptionsHub.unsubscribeFromGameStartedEvents(id)
    }

    private suspend fun unsubscribeFromGameEventsImpl(gameId: Int){
        if(!GamesStorage.contains(gameId)) return

        val game = GamesStorage.getGame(gameId)
        game.unsubscribe(this)
        subscribedOnGames.remove(gameId)
    }

    private suspend fun unsubscribeFromAllGames(){
        subscribedOnGames.toList().forEach {
            unsubscribeFromGameEventsImpl(it)
        }
    }

    private fun checkCurrentGameIsNull(errorMessage: String){
        if(currentGame != null) userFault(errorMessage)
    }

}