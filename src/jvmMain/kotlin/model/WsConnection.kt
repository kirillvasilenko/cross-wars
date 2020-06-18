package model

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class WsConnection(
        val userId: Int,
        private val incoming: ReceiveChannel<Frame>,
        private val outgoing: SendChannel<Frame>){

    private var log = LoggerFactory.getLogger(javaClass)

    private var eventsChannel =
        Channel<GameEvent>(Channel.UNLIMITED)

    suspend fun listen(){
        try {
            log.debug("start listening messages")
            listenImpl()
        }
        catch(e: Throwable){
            log.warn("error in listening messages: ${e.message}")
        }
        log.debug("stop listening messages")
    }

    fun runSendingEvents(scope: CoroutineScope){
        scope.launch {
            log.debug("start sending messages")
            try {
                for (event in eventsChannel) {
                    sendEventImpl(event)
                }
            }
            catch(ignore: CancellationException){}
            catch (e: Throwable) {
                log.warn("error in sending messages: ${e.message}")
            }
            log.debug("stop sending messages")
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
        log.debug("sent $event")
        val message = EventsSerializer.stringify(event)
        outgoing.send(Frame.Text(message))
        log.debug("Sent $message")
    }
}