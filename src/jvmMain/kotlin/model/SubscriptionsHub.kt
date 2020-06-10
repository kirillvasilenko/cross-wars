package model

import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import java.util.concurrent.ConcurrentHashMap

open class SubscriptionsHubInMemory{

    private val connectionsByUserId =
        ConcurrentHashMap<Int, WsConnection>()

    private val commonEventsSubscribers : MutableSet<WsConnection> =
        ConcurrentHashMap.newKeySet()

    private val certainGameEventsSubscribers: MutableMap<Int, MutableSet<WsConnection>> =
        ConcurrentHashMap<Int, MutableSet<WsConnection>>()

    fun createConnection(userId: Int, incoming: ReceiveChannel<Frame>, outgoing: SendChannel<Frame>) : WsConnection {
        val connection = WsConnection(userId, incoming, outgoing)
        connectionsByUserId[userId] = connection
        return connection
    }

    fun deleteConnection(userId: Int) {
        connectionsByUserId.remove(userId)

    }

    fun subscribeOnCommonEvents(userId: Int) {
        val connection = getConnection(userId)
        commonEventsSubscribers.add(connection)
    }

    fun unsubscribeFromCommonEvents(userId: Int) {
        val connection = connectionsByUserId[userId]
            ?: return
        commonEventsSubscribers.remove(connection)
    }

    fun subscribeOnGameEvents(userId: Int, gameId: Int) {
        val connection = getConnection(userId)

        var gameSubscribers = certainGameEventsSubscribers[gameId]
        if(gameSubscribers == null){
            gameSubscribers = ConcurrentHashMap.newKeySet()
            certainGameEventsSubscribers[gameId] = gameSubscribers
        }

        gameSubscribers!!.add(connection)
    }

    fun unsubscribeFromGameEvents(userId: Int, gameId: Int) {
        val connection = connectionsByUserId[userId]
            ?: return
        val gameSubscribers = certainGameEventsSubscribers[gameId]
            ?: return
        gameSubscribers.remove(connection)
    }

    suspend fun handleGameEvent(event: GameEvent){
        if(event is CommonGameEvent)
            handleCommonEvent(event)
        if(event is CertainGameEvent)
            handleCertainGameEvent(event)
        if(event is SpecificUserOnlyEvent) {
            handleSpecificUserOnlyEvent(event)
        }
    }

    private fun getConnection(userId: Int): WsConnection =
        connectionsByUserId[userId]
            ?: throw ConnectionNotFoundException("ws connection for user $userId not found.")

    private suspend fun handleSpecificUserOnlyEvent(event: SpecificUserOnlyEvent){
        val connection = connectionsByUserId[event.userId]
            ?: return
        connection.send(event)
    }

    private suspend fun handleCommonEvent(event: CommonGameEvent){
        for(connection in commonEventsSubscribers){
            connection.send(event)
        }
        if(event is GameStateChanged
            && event.actualState == GameState.ARCHIVED
        ){
            handleGameArchived(event.gameId)
        }
    }

    private suspend fun handleCertainGameEvent(event: CertainGameEvent){
        val subscribers = certainGameEventsSubscribers[event.gameId]
            ?: return

        for(connection in subscribers){
            connection.send(event)
        }
    }

    private fun handleGameArchived(gameId: Int){
        certainGameEventsSubscribers.remove(gameId)
    }
}

object SubscriptionsHub:SubscriptionsHubInMemory()

open class ConnectionNotFoundException(message: String):Throwable(message)