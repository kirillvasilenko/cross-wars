package api

import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.client.features.websocket.ws
import io.ktor.client.request.delete
import io.ktor.client.request.put
import io.ktor.http.HttpMethod

class SubscriptionsApi{

    suspend fun openWsConnection(block: suspend DefaultClientWebSocketSession.() -> Unit){
        client.ws(
                method = HttpMethod.Get,
                host = hostname,
                port = port.toInt(),
                path = "/api/ws",
                block = block
        )
    }

    suspend fun subscribeOnGameStartedEvents(){
        client.put<Unit>("$endpoint/api/games/started-subscription")
    }

    suspend fun unsubscribeFromGameStartedEvents(){
        client.delete<Unit>("$endpoint/api/games/started-subscription")
    }

    suspend fun subscribeOnGameEvents(gameId: Int){
        client.put<Unit>("$endpoint/api/games/$gameId/subscription")
    }

    suspend fun unsubscribeFromGameEvents(gameId: Int){
        client.delete<Unit>("$endpoint/api/games/$gameId/subscription")
    }
}