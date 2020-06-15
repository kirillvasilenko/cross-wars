package routes

import io.ktor.application.call
import io.ktor.http.cio.websocket.*
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.put
import io.ktor.routing.route
import io.ktor.websocket.pinger
import io.ktor.websocket.webSocket
import kotlinx.coroutines.launch
import log
import model.SubscriptionsService
import model.UserFaultException
import kotlin.time.toKotlinDuration

fun Route.webSocketConnection() {
    webSocket("/ws") {
        try {
            log.debug("Open ws connection ${getUserId()}")
            SubscriptionsService.connect(getUserId(), incoming, outgoing)
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

fun Route.subscribeOnCommonEvents(){
    route("/subscriptions/common"){
        put{
            try {
                SubscriptionsService.subscribeOnCommonEvents(getUserId())
                call.respond("Success")
            }
            catch(e: UserFaultException){
                badRequest(e)
            }
        }
        delete{
            try {
                SubscriptionsService.unsubscribeFromCommonEvents(getUserId())
                call.respond("Success")
            }
            catch(e: UserFaultException){
                badRequest(e)
            }
        }
    }
}

fun Route.subscribeOnGameEvents(){
    route("/subscriptions/current"){
        put(){
            try {
                SubscriptionsService.subscribeOnCurrentGameEvents(getUserId())
                call.respond("Success")
            }
            catch(e: UserFaultException){
                badRequest(e)
            }
        }
        delete{
            try {
                SubscriptionsService.unsubscribeFromCurrentGameEvents(getUserId())
                call.respond("Success")
            }
            catch(e: UserFaultException){
                badRequest(e)
            }
        }
    }
}

fun Route.registerSubscriptions() {
    webSocketConnection()
    subscribeOnCommonEvents()
}
