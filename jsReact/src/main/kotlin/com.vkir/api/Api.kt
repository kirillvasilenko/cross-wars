package com.vkir.api

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.websocket.WebSockets
import kotlinx.browser.window


val endpoint = window.location.origin // only needed until https://github.com/ktorio/ktor/issues/1695 is resolved
val hostname = window.location.hostname
val port = window.location.port

val client = HttpClient {
    install(JsonFeature) { serializer = KotlinxSerializer() }
    install(WebSockets)
}

object Api{
    val auth = AuthApi()
    val account = AccountApi()
    val users = UsersApi()
    val ws = WsConnectionsApi()
    val games = GamesApi()
}





