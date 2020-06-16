import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.ws
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import model.GameDto
import model.UserDto

import kotlin.browser.window
import kotlin.time.Duration

val endpoint = window.location.origin // only needed until https://github.com/ktorio/ktor/issues/1695 is resolved
val hostname = window.location.hostname
val port = window.location.port

val client = HttpClient {
    install(JsonFeature) { serializer = KotlinxSerializer() }
    install(WebSockets)
}

object Api{
    val auth = AuthApi()
    val users = UsersApi()
    val subscriptions = SubscriptionsApi()
    val games = GamesApi()
}


class AuthApi{
    suspend fun auth(): UserDto {
        return client.put("$endpoint/api/auth"){
            headers{
                header("credentials", "same-origin")
            }
        }
    }
}

class UsersApi{
    suspend fun getUser(userId: Int): UserDto {
        return client.get("$endpoint/api/users/$userId")
    }
}


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





