package model

import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap


open class SubscriptionsHubInMemory{

    private val log = LoggerFactory.getLogger(javaClass)


    /**
     * User id to subscribers
     * */
    private val userEventsSubscribers =
        ConcurrentHashMap<Int, MutableSet<WsConnection>>()

    /**
     * User id to subscribers
     * */
    private val gamesStartedSubscribers =
        ConcurrentHashMap<Int, MutableSet<WsConnection>>()

    /**
     * Game id to map from user id to subscribers
     * */
    private val gameEventsSubscribers =
        ConcurrentHashMap<Int, ConcurrentHashMap<Int, MutableSet<WsConnection>>>()


    fun createConnection(userId: Int, incoming: ReceiveChannel<Frame>, outgoing: SendChannel<Frame>) : WsConnection {
        return WsConnection(userId, incoming, outgoing)
    }

    fun subscribeOnGameStartedEvents(userId: Int, connection: WsConnection) {
        gamesStartedSubscribers
            .getOrPut(userId, { ConcurrentHashMap.newKeySet() })
            .add(connection)
        log.debug("user=$userId subscribed on game started events")
    }

    fun unsubscribeFromGameStartedEvents(userId: Int, connection: WsConnection) {
        gamesStartedSubscribers[userId]
            ?.remove(connection)
        log.debug("user=$userId unsubscribed from game started events")
    }

    fun subscribeOnGameEvents(userId: Int, gameId: Int, connection: WsConnection) {
        gameEventsSubscribers
            .getOrPut(gameId, { ConcurrentHashMap<Int, MutableSet<WsConnection>>() })
            .getOrPut(userId, { ConcurrentHashMap.newKeySet() })
            .add(connection)

        log.debug("user=$userId subscribed on game=$gameId events")
    }

    fun unsubscribeFromGameEvents(userId: Int, gameId: Int, connection: WsConnection) {
        gameEventsSubscribers[gameId]
            ?.get(userId)
            ?.remove(connection)

        log.debug("user=$userId unsubscribed from game=$gameId events")
    }

    fun subscribeOnUserEvents(userId: Int, connection: WsConnection) {
        userEventsSubscribers
            .getOrPut(userId, { ConcurrentHashMap.newKeySet() })
            .add(connection)
    }

    fun unsubscribeFromUserEvents(userId: Int, connection: WsConnection) {
        userEventsSubscribers[userId]
            ?.remove(connection)
    }

    suspend fun handleGameEvent(event: GameEvent){
        when(event){
            is GameStarted -> handle(event)
            else -> handle(event)
        }
    }

    suspend fun handleUserEvent(event: UserEvent){
        userEventsSubscribers[event.userId]?.forEach { it.send(event) }
    }

    private suspend fun handle(event: GameStarted){
        gamesStartedSubscribers.values
            .flatten()
            .forEach{ it.send(event) }
    }

    private suspend fun handle(event: GameEvent){
        gameEventsSubscribers[event.gameId]
            ?.flatMap { it.value }
            ?.forEach { it.send(event) }

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
