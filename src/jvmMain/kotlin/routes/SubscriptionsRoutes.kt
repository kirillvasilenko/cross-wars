package routes

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.put
import io.ktor.routing.route
import io.ktor.websocket.webSocket
import model.GamesService
import model.SubscriptionsService
import model.UserFaultException

fun Route.webSocketConnection() {
    webSocket("/ws") {
        try {
            SubscriptionsService.connect(getUserId(), incoming, outgoing)
        }
        catch(e: UserFaultException){
            badRequest(e)
        }
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

fun Route.registerSubscriptions() {
    webSocketConnection()
    subscribeOnCommonEvents()
}
