package com.vkir.viewModels

import com.vkir.model.*
import com.vkir.viewModels.common.FrontendEvent
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

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
            connection.sendCommand(SubscribeOnGameStartedEvents())
        }
    }

    suspend fun unsubscribeFromGameStartedEvents(eventHandler: BackendEventsHandler<GameStarted>){
        gameStartedEventsHandlers.remove(eventHandler)
        if(connection.connected){
            connection.sendCommand(UnsubscribeFromGameStartedEvents())
        }
    }

    suspend fun subscribeOnUserEvents(eventHandler: BackendEventsHandler<UserEvent>){
        userEventsHandlers.add(eventHandler)
        if(connection.connected){
            connection.sendCommand(SubscribeOnUserEvents())
        }
    }

    suspend fun unsubscribeFromUserEvents(eventHandler: BackendEventsHandler<UserEvent>){
        userEventsHandlers.remove(eventHandler)
        if(connection.connected){
            connection.sendCommand(UnsubscribeFromUserEvents())
        }
    }

    suspend fun subscribeOnGameEvents(
            gameId: Int,
            eventHandler: BackendEventsHandler<GameEvent>){

        gameEventsHandlers[gameId] = eventHandler
        if(connection.connected){
            connection.sendCommand(SubscribeOnGameEvents(gameId))
        }
    }

    suspend fun unsubscribeFromGameEvents(gameId: Int){
        gameEventsHandlers.remove(gameId)
        if(connection.connected){
            connection.sendCommand(UnsubscribeFromGameEvents(gameId))
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
                log.error(e) { "error on handling GameStarted event: ${e.message}" }
            }
        }
    }

    private suspend fun handle(event: UserEvent){
        userEventsHandlers.forEach {
            try {
                it.handle(event)
            } catch (e: Throwable) {
                log.error(e) { "error on handling UserEvent: ${e.message}" }
            }
        }
    }

    private suspend fun handle(event: GameEvent){
        val handler = gameEventsHandlers[event.gameId]
        if(handler == null){
            log.error { "handler not found for $event" }
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
