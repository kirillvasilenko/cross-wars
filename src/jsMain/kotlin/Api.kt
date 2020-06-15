import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
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
        return client.put("$endpoint/api/auth")
    }
}

class UsersApi{
    suspend fun getUser(userId: Int): UserDto {
        return client.get("$endpoint/api/users/$userId")
    }
}

data class OpenWsArgs(
        val handleText: suspend (String) -> Unit,
    val onConnectionSet: suspend () -> Unit
)

class SubscriptionsApi{

    suspend fun openWsConnection(args: OpenWsArgs){
        client.ws(
                method = HttpMethod.Get,
                host = hostname,
                port = port.toInt(),
                path = "/api/ws"
        ) {
            //args.onConnectionSet()
            /*val closeFrame = Frame.Close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, "Ping timeout"))
            outgoing.send(closeFrame)*/
            //this.close()
            /*println("open ws connection")
            outgoing.send(Frame.Text("hello"))*/
            println("Sent hello through ws.")
            for(frame in incoming){
                when(frame){
                    is Frame.Text -> println("ws received ${frame.readText()}")//args.handleText(frame.readText())
                    else -> println("ws received ${frame.frameType}")
                }
            }
        }
    }

    suspend fun subscribeOnCommonEvents(){
        client.put<Unit>("/api/subscriptions/common")
    }

    suspend fun unsubscribeFromCommonEvents(){
        client.delete<Unit>("/api/subscriptions/common")
    }

    suspend fun subscribeOnCurrentGameEvents(){
        client.put<Unit>("/api/subscriptions/current")
    }

    suspend fun unsubscribeFromCurrentGameEvents(){
        client.delete<Unit>("/api/subscriptions/current")
    }
}

class GamesApi{

    suspend fun getGame(gameId: Int): GameDto {
        return client.get("$endpoint/api/games/$gameId")
    }

    suspend fun getGames(): List<GameDto> {
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





