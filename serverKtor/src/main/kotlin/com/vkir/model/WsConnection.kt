package com.vkir.model

import io.ktor.websocket.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

class WsConnection(
    val userId: Int,
    private val incoming: ReceiveChannel<Frame>,
    private val outgoing: SendChannel<Frame>
) {

    private var eventsChannel =
        Channel<BackendEvent>(Channel.UNLIMITED)

    suspend fun listenAndSend(): Unit = coroutineScope {
        launch {
            sendEvents()
        }
        launch {
            listenMessages()
        }
        Unit
    }

    suspend fun send(event: BackendEvent) {
        eventsChannel.send(event)
    }

    private suspend fun listenMessages() {
        try {
            log.debug("start listening messages")
            listenImpl()
        } catch (e: Throwable) {
            log.warn("error in listening messages: ${e.message}")
        }
        unsubscribeAll()
        log.debug("stop listening messages")
    }

    private suspend fun sendEvents() {
        log.debug("start sending messages")
        try {
            for (event in eventsChannel) {
                sendEventImpl(event)
            }
        } catch (ignore: CancellationException) {
        } catch (e: Throwable) {
            log.warn("error in sending messages: ${e.message}")
        }
        log.debug("stop sending messages")
    }

    private suspend fun listenImpl() {
        for (frame in incoming) {
            when (frame) {
                is Frame.Text -> handleText(frame.readText())
                else -> log.debug("Received from user=$userId through ws: $frame")
            }
        }
    }

    private suspend fun handleText(text: String) {
        try {
            val command = WsCommandsSerializer.parse(text)
            log.debug("Received command: $command")
            when (command) {
                is SubscribeOnUserEvents -> SubscriptionsHub.subscribeOnUserEvents(userId, this)
                is UnsubscribeFromUserEvents -> SubscriptionsHub.unsubscribeFromUserEvents(userId, this)
                is SubscribeOnGameStartedEvents -> SubscriptionsHub.subscribeOnGameStartedEvents(userId, this)
                is UnsubscribeFromGameStartedEvents -> SubscriptionsHub.unsubscribeFromGameStartedEvents(userId, this)
                is SubscribeOnGameEvents -> subscribeOnGameEvents(command.gameId)
                is UnsubscribeFromGameEvents -> unsubscribeFromGameEvents(command.gameId)
            }
        } catch (e: Throwable) {
            log.warn("error on parsing text command: ${e.message}")
        }
    }

    private val subscribedGames = mutableSetOf<Int>()

    private suspend fun subscribeOnGameEvents(gameId: Int) {
        SubscriptionsHub.subscribeOnGameEvents(userId, gameId, this)
        subscribedGames.add(gameId)
        val game = GamesStorage.getGame(gameId)
        game.sendSnapshot(this)
    }

    private fun unsubscribeFromGameEvents(gameId: Int) {
        SubscriptionsHub.unsubscribeFromGameEvents(userId, gameId, this)
        subscribedGames.remove(gameId)
    }

    private suspend fun sendEventImpl(event: BackendEvent) {
        val message = EventsSerializer.stringify(event)
        outgoing.send(Frame.Text(message))
        log.debug("Sent $message")
    }

    private fun unsubscribeAll() {
        try {
            SubscriptionsHub.unsubscribeFromUserEvents(userId, this)
            SubscriptionsHub.unsubscribeFromGameStartedEvents(userId, this)
            subscribedGames.forEach { gameId ->
                SubscriptionsHub.unsubscribeFromGameEvents(userId, gameId, this)
            }
            subscribedGames.clear()
        } catch (e: Throwable) {
            log.error("error on unsubscribeAll: ${e.message}")
        }
    }
}