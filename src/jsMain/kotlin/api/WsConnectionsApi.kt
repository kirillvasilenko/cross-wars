package api

import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.client.features.websocket.ws
import io.ktor.client.request.delete
import io.ktor.client.request.put
import io.ktor.http.HttpMethod

class WsConnectionsApi{

    suspend fun openWsConnection(block: suspend DefaultClientWebSocketSession.() -> Unit){
        client.ws(
                method = HttpMethod.Get,
                host = hostname,
                port = port.toInt(),
                path = "/api/ws",
                block = block
        )
    }
}