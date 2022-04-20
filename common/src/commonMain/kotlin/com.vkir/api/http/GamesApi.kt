package com.vkir.api.http

import com.vkir.model.GameDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put

class GamesApi(private val client: HttpClient) {

    suspend fun getGame(gameId: Int): GameDto {
        return client.get("/api/games/$gameId").body()
    }

    suspend fun getActiveGames(): List<GameDto> {
        return client.get("/api/games").body()
    }

    suspend fun startNewGame(): GameDto {
        return client.post("/api/games/start-new").body()
    }

    suspend fun joinGame(gameId: Int): GameDto {
        return client.put("/api/games/$gameId/join").body()
    }

    suspend fun leaveCurrentGame() {
        client.put("/api/games/leave")
    }

    suspend fun makeMove(x: Int, y: Int) {
        client.put("/api/games/move?x=$x&y=$y")
    }

}