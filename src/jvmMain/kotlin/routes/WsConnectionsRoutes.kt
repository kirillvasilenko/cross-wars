package routes

import app.WsConnectionsService
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.close
import io.ktor.routing.Route
import io.ktor.websocket.webSocket
import model.UserFaultException
import org.slf4j.LoggerFactory

fun Route.webSocketConnection() {
    val log = LoggerFactory.getLogger("SubscriptionsRoutes")
    webSocket("/ws") {
        try {
            log.debug("Open ws connection ${getUserId()}")
            WsConnectionsService.connect(getUserId(), incoming, outgoing)
        }
        catch(e: UserFaultException){
            close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, e.message!!))
        }
        catch(e: Throwable){
            close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, e.message ?: "internal server error"))
        }
        log.debug("Close ws connection ${getUserId()}")
    }
}

fun Route.registerWsConnections() {
    webSocketConnection()
}
