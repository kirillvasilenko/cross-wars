package model

import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

open class SubscriptionsServiceInMemory{

    suspend fun connect(userId: Int, incoming: ReceiveChannel<Frame>, outgoing: SendChannel<Frame>){
        val user = UsersStorage.getUser(userId)
        user.connect(incoming, outgoing)
    }

    suspend fun subscribeOnCommonEvents(userId: Int) {
        val user = UsersStorage.getUser(userId)
        user.subscribeOnCommonEvents()
    }

    suspend fun unsubscribeFromCommonEvents(userId: Int) {
        val user = UsersStorage.getUser(userId)
        user.unsubscribeFromCommonEvents()
    }

    suspend fun subscribeOnCurrentGameEvents(userId: Int) {
        val user = UsersStorage.getUser(userId)
        user.subscribeOnCurrentGameEvents()
    }

    suspend fun unsubscribeFromCurrentGameEvents(userId: Int) {
        val user = UsersStorage.getUser(userId)
        user.unsubscribeFromCurrentGameEvents()
    }
}

object SubscriptionsService:SubscriptionsServiceInMemory()


