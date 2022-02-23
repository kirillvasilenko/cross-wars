package com.vkir.api.http

import com.vkir.model.GameDto
import io.ktor.client.*
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put

class GamesApi(private val client: HttpClient) {

    suspend fun getGame(gameId: Int): GameDto {
        return client.get("/api/games/$gameId")
    }

    suspend fun getActiveGames(): List<GameDto> {
        return client.get("/api/games")
    }

    suspend fun startNewGame(): GameDto {
        return client.post("/api/games/start-new")
    }

    suspend fun joinGame(gameId: Int): GameDto {
        return client.put("/api/games/$gameId/join")
    }

    suspend fun leaveCurrentGame() {
        client.put<Unit>("/api/games/leave")
    }

    suspend fun makeMove(x: Int, y: Int) {
        client.put<Unit>("/api/games/move?x=$x&y=$y")
    }

}