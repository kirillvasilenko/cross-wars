package api

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import model.GameDto

class GamesApi{

    suspend fun getGame(gameId: Int): GameDto {
        return client.get("$endpoint/api/games/$gameId")
    }

    suspend fun getActiveGames(): List<GameDto> {
        return client.get("$endpoint/api/games")
    }

    suspend fun startNewGame(): GameDto {
        return client.post("$endpoint/api/games/start-new")
    }

    suspend fun joinGame(gameId: Int): GameDto {
        return client.put("$endpoint/api/games/$gameId/join")
    }

    suspend fun leaveCurrentGame() {
        client.put<Unit>("$endpoint/api/games/leave")
    }

    suspend fun makeMove(x: Int, y: Int) {
        client.put<Unit>("$endpoint/api/games/move?x=$x&y=$y")
    }

}