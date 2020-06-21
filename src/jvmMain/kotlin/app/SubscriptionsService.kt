package app

import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import model.UsersStorage
import org.slf4j.LoggerFactory
import routes.getUserId

open class SubscriptionsServiceInMemory{

    suspend fun connect(userId: Int, incoming: ReceiveChannel<Frame>, outgoing: SendChannel<Frame>){
        val user = UsersStorage.getUser(userId)
        user.connect(incoming, outgoing)
    }

    suspend fun subscribeOnGameStartedEvents(userId: Int) {
        val user = UsersStorage.getUser(userId)
        user.subscribeOnGameStartedEvents()
    }

    suspend fun unsubscribeFromGameStartedEvents(userId: Int) {
        val user = UsersStorage.getUser(userId)
        user.unsubscribeFromGameStartedEvents()
    }

    suspend fun subscribeOnGameEvents(userId: Int, gameId: Int) {
        val user = UsersStorage.getUser(userId)
        user.subscribeOnGameEvents(gameId)
    }

    suspend fun unsubscribeFromGameEvents(userId: Int, gameId: Int) {
        val user = UsersStorage.getUser(userId)
        user.unsubscribeFromGameEvents(gameId)
    }

    suspend fun subscribeOnUserEvents(userId: Int) {
        val user = UsersStorage.getUser(userId)
        user.subscribeOnUserEvents()
    }

    suspend fun unsubscribeFromUserEvents(userId: Int) {
        val user = UsersStorage.getUser(userId)
        user.unsubscribeFromUserEvents()
    }
}

object SubscriptionsService: SubscriptionsServiceInMemory()


