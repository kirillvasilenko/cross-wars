package com.vkir.app

import com.vkir.model.UsersStorage
import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

open class WsConnectionsServiceInMemory{

    suspend fun connect(userId: Int, incoming: ReceiveChannel<Frame>, outgoing: SendChannel<Frame>){
        val user = UsersStorage.getUser(userId)
        user.connect(incoming, outgoing)
    }
}

object WsConnectionsService: WsConnectionsServiceInMemory()


