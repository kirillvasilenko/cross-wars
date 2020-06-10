package model

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import log
import java.util.concurrent.ConcurrentHashMap

class WsConnection(val userId: Int, val incoming: ReceiveChannel<Frame>, val outgoing: SendChannel<Frame>){

    private var eventsChannel = Channel<GameEvent>(Channel.UNLIMITED)

    suspend fun listen(){
        try {
            listenImpl()
        }
        catch(e: Throwable){
            log.warn(e.message)
        }
    }

    suspend fun runSendingEvents() = coroutineScope {
        launch {
            try{
                for (event in eventsChannel) {
                    sendEventImpl(event)
                }
            }
            catch(e: Throwable){
                log.warn(e.message)
            }
        }
    }

    suspend fun send(event: GameEvent) {
        eventsChannel.send(event)
    }

    private suspend fun listenImpl(){
        for (frame in incoming) {
            when (frame) {
                is Frame.Text -> {
                    val text = frame.readText()
                    log.debug("Received from user=$userId through ws: $text")
                }
                else -> log.debug("Received from user=$userId through ws: $frame")
            }
        }
    }

    private suspend fun sendEventImpl(event: GameEvent){
        val message = EventsSerializer.stringify(event)
        outgoing.send(Frame.Text(message))
        log.debug("Sent $message to $userId through ws.")
    }
}


open class SubscriptionsHubInMemory{

    private val connectionsByUserId = ConcurrentHashMap<Int, WsConnection>()

    private val commonEventsSubscribers : MutableSet<WsConnection> = ConcurrentHashMap.newKeySet()

    private val certainGameEventsSubscribers: MutableMap<Int, MutableSet<WsConnection>> =  ConcurrentHashMap<Int, MutableSet<WsConnection>>()

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
            && event.actualState == GameState.ARCHIVED){
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

open class SubscriptionsServiceInMemory{

    suspend fun connect(userId: Int, incoming: ReceiveChannel<Frame>, outgoing: SendChannel<Frame>){
        val user = UsersStorage.getUser(userId)
        user.connect(incoming, outgoing)
    }

    suspend fun subscribeOnCommonEvents(userId: Int) {
        val user = UsersStorage.getUser(userId)
        user.subscribeOnCommonEvents()
    }

    suspend fun unsubscribeFromCommonEvents(userId: Int) {
        val user = UsersStorage.getUser(userId)
        user.unsubscribeFromCommonEvents()
    }

    suspend fun subscribeOnCurrentGameEvents(userId: Int) {
        val user = UsersStorage.getUser(userId)
        user.subscribeOnCurrentGameEvents()
    }

    suspend fun unsubscribeFromCurrentGameEvents(userId: Int) {
        val user = UsersStorage.getUser(userId)
        user.unsubscribeFromCurrentGameEvents()
    }
}

object SubscriptionsService:SubscriptionsServiceInMemory()

open class ConnectionNotFoundException(message: String):Exception(message)
