package com.vkir.api.http

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*

class WsConnectionsApi(private val client: HttpClient) {

    suspend fun openWsConnection(block: suspend DefaultClientWebSocketSession.() -> Unit) {
        client.ws(
            path = "/api/ws",
            block = block
        )
    }
}