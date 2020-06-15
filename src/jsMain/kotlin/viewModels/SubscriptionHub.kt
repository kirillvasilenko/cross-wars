package viewModels

import Api
import model.*

object SubscriptionHub{

    private val connection = WsConnection()

    private var gameStartedEventHandler: GameEventHandler<GameStarted> = GameEventHandler({})

    private var gameEventsHandlers = hashMapOf<Int, GameEventHandler<GameEvent>>()

    init{
        connection.textHandler = SubscriptionHub::handleText
        connection.onConnectionOpened = ::onConnectionOpened
        connection.openWsConnectionInfinite()
    }

    private suspend fun onConnectionOpened(){
        gameEventsHandlers.values.forEach {
            it.connectionOpened()
        }
        gameStartedEventHandler.connectionOpened()
    }

    private suspend fun handleText(text: String){
        val event = EventsSerializer.parse(text)
        when(event){
            is GameStarted -> gameStartedEventHandler.handle(event)
            else -> handleGameEvent(event)
        }
    }

    private suspend fun handleGameEvent(event: GameEvent){
        val handler = gameEventsHandlers[event.gameId]
        if(handler == null){
            log("handler not found for $event")
            return
        }
        handler.handle(event)
    }

    suspend fun setOnGameStartedEventsHandler(eventHandler: GameEventHandler<GameStarted>){
        gameStartedEventHandler = eventHandler
        if(connection.connected){
            Api.subscriptions.subscribeOnGameStartedEvents()
        }
    }

    suspend fun unsubscribeFromGameStartedEvents(){
        gameStartedEventHandler = GameEventHandler({})
        if(connection.connected){
            Api.subscriptions.unsubscribeFromGameStartedEvents()
        }
    }

    suspend fun subscribeOnGameEvents(
            gameId: Int,
            eventHandler: GameEventHandler<GameEvent>){

        gameEventsHandlers[gameId] = eventHandler
        if(connection.connected){
            Api.subscriptions.subscribeOnGameEvents(gameId)
        }
    }

    suspend fun unsubscribeFromGameEvents(gameId: Int){
        gameEventsHandlers.remove(gameId)
        if(connection.connected){
            Api.subscriptions.unsubscribeFromGameEvents(gameId)
        }
    }
}

class GameEventHandler<T>(
        private val eventHandler: suspend (T) -> Unit,
        private val onConnectionOpened: suspend () -> Unit = {}){

    suspend fun handle(event: T) = eventHandler(event)
    suspend fun connectionOpened() = onConnectionOpened()
}
