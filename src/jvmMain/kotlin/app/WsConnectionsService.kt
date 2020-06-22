package app

import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import model.UsersStorage

open class WsConnectionsServiceInMemory{

    suspend fun connect(userId: Int, incoming: ReceiveChannel<Frame>, outgoing: SendChannel<Frame>){
        val user = UsersStorage.getUser(userId)
        user.connect(incoming, outgoing)
    }
}

object WsConnectionsService: WsConnectionsServiceInMemory()


