package model

import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

open class SubscriptionsHubInMemory{

    private val log = LoggerFactory.getLogger(javaClass)

    private val connectionsByUserId =
        ConcurrentHashMap<Int, WsConnection>()

    private val gameEventsSubscribers: MutableMap<Int, MutableSet<WsConnection>> =
        ConcurrentHashMap<Int, MutableSet<WsConnection>>()

    private val startedGamesSubscribers: MutableSet<WsConnection> = ConcurrentHashMap.newKeySet()

    fun createConnection(userId: Int, incoming: ReceiveChannel<Frame>, outgoing: SendChannel<Frame>) : WsConnection {
        val connection = WsConnection(userId, incoming, outgoing)
        connectionsByUserId[userId] = connection
        return connection
    }

    fun deleteConnection(userId: Int) {
        connectionsByUserId.remove(userId)
    }

    fun subscribeOnGameStartedEvents(userId: Int) {
        val connection = getConnection(userId)
        startedGamesSubscribers.add(connection)
        log.debug("user=$userId subscribed on game started events")
    }

    fun unsubscribeFromGameStartedEvents(userId: Int) {
        val connection = connectionsByUserId[userId]
            ?: return
        startedGamesSubscribers.remove(connection)
        log.debug("user=$userId unsubscribed from game started events")
    }

    fun subscribeOnGameEvents(userId: Int, gameId: Int) {
        val connection = getConnection(userId)

        var gameSubscribers = gameEventsSubscribers[gameId]
        if(gameSubscribers == null){
            gameSubscribers = ConcurrentHashMap.newKeySet()
            gameEventsSubscribers[gameId] = gameSubscribers
        }

        gameSubscribers!!.add(connection)
        log.debug("user=$userId subscribed on game=$gameId events")
    }

    fun unsubscribeFromGameEvents(userId: Int, gameId: Int) {
        val connection = connectionsByUserId[userId]
            ?: return
        val gameSubscribers = gameEventsSubscribers[gameId]
            ?: return
        gameSubscribers.remove(connection)
        log.debug("user=$userId unsubscribed from game=$gameId events")
    }

    suspend fun handleGameEvent(event: GameEvent){
        when(event){
            is GameStarted -> handleGameStarted(event)
            is UserSubscribedOnGameEvents -> handleUserSubscribedOnGameEvents(event)
            else -> handleGameEventImpl(event)
        }
    }

    private fun getConnection(userId: Int): WsConnection =
        connectionsByUserId[userId]
            ?: throw ConnectionNotFoundException("ws connection for user $userId not found.")

    private suspend fun handleUserSubscribedOnGameEvents(event: UserSubscribedOnGameEvents){
        val connection = connectionsByUserId[event.userId]
            ?: return
        connection.send(event)
    }

    private suspend fun handleGameStarted(event: GameStarted){
        for(connection in startedGamesSubscribers){
            connection.send(event)
        }
    }

    private suspend fun handleGameEventImpl(event: GameEvent){
        val subscribers = gameEventsSubscribers[event.gameId]
            ?: return

        for(connection in subscribers){
            connection.send(event)
        }

        if(event is GameStateChanged
                && event.actualState == GameState.ARCHIVED){
            handleGameArchived(event.gameId)
        }
    }

    private fun handleGameArchived(gameId: Int){
        gameEventsSubscribers.remove(gameId)
    }
}

object SubscriptionsHub:SubscriptionsHubInMemory()

open class ConnectionNotFoundException(message: String):Throwable(message)