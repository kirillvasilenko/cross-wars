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

    suspend fun runSendingEvents() = coroutineScope {
        launch {
            for (event in eventsChannel) {
                sendEventImpl(event)
            }
        }
    }

    suspend fun send(event: GameEvent) {
        eventsChannel.send(event)
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

    private val certainGameEventsSubscribers: Map<Int, Set<WsConnection>> =  ConcurrentHashMap<Int, Set<WsConnection>>()

    fun createConnection(userId: Int, incoming: ReceiveChannel<Frame>, outgoing: SendChannel<Frame>) : WsConnection {
        val connection = WsConnection(userId, incoming, outgoing)
        connectionsByUserId[userId] = connection
        return connection
    }

    fun deleteConnection(userId: Int) {
        connectionsByUserId.remove(userId)
    }

    fun subscribeOnCommonEvents(userId: Int) {
        val connection = connectionsByUserId[userId]
            ?: throw ConnectionNotFoundException("ws connection for user $userId not found.")
        commonEventsSubscribers.add(connection)
    }

    fun unsubscribeFromCommonEvents(userId: Int) {
        val connection = connectionsByUserId[userId]
            ?: return
        commonEventsSubscribers.remove(connection)
    }

    suspend fun handleGameEvent(event: GameEvent){
        if(event is CommonGameEvent)
            sendCommonEvent(event)
        if(event is CertainGameEvent)
            sendCertainGameEvent(event)
    }

    private suspend fun sendCommonEvent(event: CommonGameEvent){
        for(connection in commonEventsSubscribers){
            connection.send(event)
        }
    }

    private suspend fun sendCertainGameEvent(event: CertainGameEvent){
        val subscribers = certainGameEventsSubscribers[event.gameId]
            ?: return

        for(connection in subscribers){
            connection.send(event)
        }
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
}

object SubscriptionsService:SubscriptionsServiceInMemory()

open class ConnectionNotFoundException(message: String):Exception(message)
