import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.websocket.webSocket
import model.GameDto
import model.UserDto

import kotlin.browser.window

val endpoint = window.location.origin // only needed until https://github.com/ktorio/ktor/issues/1695 is resolved

val jsonClient = HttpClient {
    install(JsonFeature) { serializer = KotlinxSerializer() }
}

object Api{
    val auth = AuthApi()
    val users = UsersApi()
    val subscriptions = SubscriptionsApi()
    val games = GamesApi()
}


class AuthApi{
    suspend fun auth(): UserDto {
        return jsonClient.put("$endpoint/api/auth")
    }
}

class UsersApi{
    suspend fun getUser(userId: Int): UserDto {
        return jsonClient.get("$endpoint/api/users/$userId")
    }
}

class SubscriptionsApi{
    suspend fun subscribeOnCommonEvents(){
        jsonClient.put<Unit>("/api/subscriptions/common")
    }

    suspend fun unsubscribeFromCommonEvents(){
        jsonClient.delete<Unit>("/api/subscriptions/common")
    }

    suspend fun subscribeOnCurrentGameEvents(){
        jsonClient.put<Unit>("/api/subscriptions/current")
    }

    suspend fun unsubscribeFromCurrentGameEvents(){
        jsonClient.delete<Unit>("/api/subscriptions/current")
    }
}

class GamesApi{

    suspend fun getGame(gameId: Int): GameDto {
        return jsonClient.get("$endpoint/api/games/$gameId")
    }

    suspend fun getGames(): List<GameDto> {
        return jsonClient.get("$endpoint/api/games")
    }

    suspend fun startNewGame(): GameDto {
        return jsonClient.post("$endpoint/api/games/start-new")
    }

    suspend fun joinGame(gameId: Int): GameDto {
        return jsonClient.put("$endpoint/api/games/$gameId/join")
    }

    suspend fun leaveCurrentGame() {
        jsonClient.put<Unit>("$endpoint/api/games/leave")
    }

    suspend fun makeMove(x: Int, y: Int) {
        jsonClient.put<Unit>("$endpoint/api/games/move?x=$x&y=$y")
    }

}





