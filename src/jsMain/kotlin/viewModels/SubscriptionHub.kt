package viewModels

import Api
import model.*

object SubscriptionHub{

    private val connection = WsConnection()

    private var commonEventsHandler: suspend (GameEvent) -> Unit = {}

    private var currentGameEventsHandler: suspend (GameEvent) -> Unit = {}

    init{
        connection.textHandler = SubscriptionHub::handleText
        connection.openWsConnectionInfinite()
    }

    private suspend fun handleText(text: String){
        val event = EventsSerializer.parse(text)
        log(event.toString())
        if(event is CertainGameEvent){
            currentGameEventsHandler(event)
        }
        if(event is CommonGameEvent){
            commonEventsHandler(event)
        }
        if(event is SpecificUserOnlyEvent){
            currentGameEventsHandler(event)
            commonEventsHandler(event)
        }
    }

    suspend fun subscribeCommonEvents(
            eventHandler: suspend (GameEvent) -> Unit,
            connectionOpenedHandler: suspend () -> Unit){
        // only common or current game, no both of them
        commonEventsHandler = eventHandler
        currentGameEventsHandler = {}
        connection.onConnectionOpened = {
            connectionOpenedHandler()
            Api.subscriptions.subscribeOnCommonEvents()
        }
        if(connection.connected){
            Api.subscriptions.subscribeOnCommonEvents()
        }
    }

    suspend fun subscribeCurrentGameEvents(
            eventHandler: suspend (GameEvent) -> Unit,
            connectionOpenedHandler: suspend () -> Unit){
        // only common or current game, no both of them
        commonEventsHandler = {}
        currentGameEventsHandler = eventHandler
        connection.onConnectionOpened = {
            connectionOpenedHandler()
            Api.subscriptions.subscribeOnCurrentGameEvents()
        }
        if(connection.connected){
            Api.subscriptions.subscribeOnCurrentGameEvents()
        }
    }

}