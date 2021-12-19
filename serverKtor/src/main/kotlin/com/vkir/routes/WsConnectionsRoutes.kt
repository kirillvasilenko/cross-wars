package com.vkir.routes


import com.vkir.app.WsConnectionsService
import com.vkir.model.UserFaultException
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.close
import io.ktor.routing.Route
import io.ktor.websocket.*
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

fun Route.webSocketConnection() {
    webSocket("/ws") {
        try {
            log.debug("Open ws connection ${getUserId()}")
            WsConnectionsService.connect(getUserId(), incoming, outgoing)
        }
        catch(e: UserFaultException){
            close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, e.message ?: ""))
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
