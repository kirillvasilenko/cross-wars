package routes

import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import io.ktor.routing.Route
import io.ktor.websocket.webSocket
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import log

class Connector(val incoming: ReceiveChannel<Frame>, val outgoing: SendChannel<Frame>){

}

fun Route.connectionRouting() {
    webSocket("/ws") {
        val connector = Connector(incoming, outgoing)
        for (frame in incoming) {
            when (frame) {
                is Frame.Text -> {
                    val text = frame.readText()
                    log.debug("Received from ws: $text")
                }
                else -> log.debug("Received from ws: $frame")
            }
        }
    }
}

fun Route.registerSubscriptions() {
    connectionRouting()
}
