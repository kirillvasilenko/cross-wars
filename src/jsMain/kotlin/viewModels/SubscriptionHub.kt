package viewModels

import api.Api
import log
import model.*
import viewModels.common.FrontendEvent

object SubscriptionHub{

    private val connection = WsConnection()

    private var frontendEventsHandlers = mutableSetOf<FrontendEventsHandler>()

    private var gameStartedEventsHandlers = mutableSetOf<BackendEventsHandler<GameStarted>>()

    private var userEventsHandlers = mutableSetOf<BackendEventsHandler<UserEvent>>()

    private var gameEventsHandlers = hashMapOf<Int, BackendEventsHandler<GameEvent>>()

    init{
        connection.textHandler = SubscriptionHub::handleText
        connection.onConnectionOpened = SubscriptionHub::onConnectionOpened
    }

    suspend fun subscribeOnGameStartedEvents(eventHandler: BackendEventsHandler<GameStarted>){
        gameStartedEventsHandlers.add(eventHandler)
        if(connection.connected){
            Api.subscriptions.subscribeOnGameStartedEvents()
        }
    }

    suspend fun unsubscribeFromGameStartedEvents(eventHandler: BackendEventsHandler<GameStarted>){
        gameStartedEventsHandlers.remove(eventHandler)
        if(connection.connected){
            Api.subscriptions.unsubscribeFromGameStartedEvents()
        }
    }

    suspend fun subscribeOnUserEvents(eventHandler: BackendEventsHandler<UserEvent>){
        userEventsHandlers.add(eventHandler)
        if(connection.connected){
            Api.subscriptions.subscribeOnUserEvents()
        }
    }

    suspend fun unsubscribeFromUserEvents(eventHandler: BackendEventsHandler<UserEvent>){
        userEventsHandlers.remove(eventHandler)
        if(connection.connected){
            Api.subscriptions.unsubscribeFromUserEvents()
        }
    }

    suspend fun subscribeOnGameEvents(
            gameId: Int,
            eventHandler: BackendEventsHandler<GameEvent>){

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

    fun subscribeOnFrontendEvents(handler: FrontendEventsHandler){
        frontendEventsHandlers.add(handler)
    }

    fun unsubscribeFromFrontendEvents(handler: FrontendEventsHandler){
        frontendEventsHandlers.remove(handler)
    }

    suspend fun raiseEvent(event: FrontendEvent){
        frontendEventsHandlers.forEach { it.handle(event) }
    }

    fun startConnecting(){
        connection.startWsConnecting()
    }

    suspend fun stopConnecting(){
        connection.stopWsConnecting()
    }


    private suspend fun onConnectionOpened(){
        gameEventsHandlers.values.forEach {
            it.connectionOpened()
        }
        userEventsHandlers.forEach { it.connectionOpened() }
        gameStartedEventsHandlers.forEach { it.connectionOpened() }
    }

    private suspend fun handleText(text: String){
        when(val event = EventsSerializer.parse(text)){
            is GameStarted -> handle(event)
            is GameEvent -> handle(event)
            is UserEvent -> handle(event)
        }
    }

    private suspend fun handle(event: GameStarted){
        gameStartedEventsHandlers.forEach {
            try {
                it.handle(event)
            } catch (e: Throwable) {
                log("error on handling GameStarted event: ${e.message}")
            }
        }
    }

    private suspend fun handle(event: UserEvent){
        log("received UserEvent: $event")
        userEventsHandlers.forEach {
            try {
                it.handle(event)
            } catch (e: Throwable) {
                log("error on handling UserEvent: ${e.message}")
            }
        }
    }

    private suspend fun handle(event: GameEvent){
        val handler = gameEventsHandlers[event.gameId]
        if(handler == null){
            log("handler not found for $event")
            return
        }
        handler.handle(event)
    }

}

class FrontendEventsHandler(
    private val eventHandler: suspend (FrontendEvent) -> Unit){

    suspend fun handle(event: FrontendEvent) = eventHandler(event)
}

class BackendEventsHandler<T>(
    private val eventHandler: suspend (T) -> Unit,
    private val onConnectionOpened: suspend () -> Unit = {}
)
        where T : BackendEvent {

    suspend fun handle(event: T) = eventHandler(event)
    suspend fun connectionOpened() = onConnectionOpened()
}
