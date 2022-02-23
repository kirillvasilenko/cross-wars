package com.vkir.api.http

import io.ktor.client.HttpClient

class Api(private val client: HttpClient) {
    val auth = AuthApi(client)
    val account = AccountApi(client)
    val users = UsersApi(client)
    val ws = WsConnectionsApi(client)
    val games = GamesApi(client)
}





