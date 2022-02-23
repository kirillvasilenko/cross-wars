package com.vkir.api.http

import com.vkir.utils.generateGuid
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*

class WsConnectionsApi(private val client: HttpClient) {

    suspend fun openWsConnection(block: suspend DefaultClientWebSocketSession.() -> Unit) {
        client.ws(
            path = "/api/ws",
            block = block
        )
    }
}