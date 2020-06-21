package routes

import io.ktor.application.call
import io.ktor.http.cio.websocket.*
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.put
import io.ktor.routing.route
import io.ktor.websocket.webSocket
import log
import app.SubscriptionsService
import model.UserFaultException
import org.slf4j.LoggerFactory

fun Route.webSocketConnection() {
    val log = LoggerFactory.getLogger("SubscriptionsRoutes")
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

fun Route.subscribeOnGameStartedEvents(){
    route("/games/started-subscription"){
        put{
            try {
                SubscriptionsService.subscribeOnGameStartedEvents(getUserId())
                call.respond("Success")
            }
            catch(e: UserFaultException){
                log("error on subscribeOnCommonEvents: ${e.message}")
                badRequest(e)
            }
        }
        delete{
            try {
                SubscriptionsService.unsubscribeFromGameStartedEvents(getUserId())
                call.respond("Success")
            }
            catch(e: UserFaultException){
                badRequest(e)
            }
        }
    }
}

fun Route.subscribeOnGameEvents(){
    route("/games/{id}/subscription"){
        put(){
            try {
                SubscriptionsService.subscribeOnGameEvents(getUserId(), getIntFromParams("id"))
                call.respond("Success")
            }
            catch(e: UserFaultException){
                badRequest(e)
            }
        }
        delete{
            try {
                SubscriptionsService.unsubscribeFromGameEvents(getUserId(), getIntFromParams("id"))
                call.respond("Success")
            }
            catch(e: UserFaultException){
                badRequest(e)
            }
        }
    }
}

fun Route.subscribeOnUserEvents(){
    route("/account/subscription"){
        put{
            try {
                SubscriptionsService.subscribeOnUserEvents(getUserId())
                call.respond("Success")
            }
            catch(e: UserFaultException){
                badRequest(e)
            }
        }
        delete{
            try {
                SubscriptionsService.unsubscribeFromUserEvents(getUserId())
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
    subscribeOnGameStartedEvents()
    subscribeOnGameEvents()
    subscribeOnUserEvents()
}
