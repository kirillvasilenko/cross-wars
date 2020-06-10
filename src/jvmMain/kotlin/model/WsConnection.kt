package model

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import log

class WsConnection(val userId: Int, val incoming: ReceiveChannel<Frame>, val outgoing: SendChannel<Frame>){

    private var eventsChannel =
        Channel<GameEvent>(Channel.UNLIMITED)

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
            try {
                for (event in eventsChannel) {
                    sendEventImpl(event)
                }
            } catch (e: Throwable) {
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