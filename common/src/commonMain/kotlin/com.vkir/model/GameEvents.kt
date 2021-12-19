package com.vkir.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object EventsSerializer{

    private val json = Json.Default

    fun stringify(event: BackendEvent) = json.encodeToString(BackendEvent.serializer(), event)

    fun parse(eventAsString: String) = json.decodeFromString(BackendEvent.serializer(), eventAsString)
}

object WsCommandsSerializer{

    private val json = Json.Default

    fun stringify(command: WsCommand) = json.encodeToString(WsCommand.serializer(), command)

    fun parse(commandAsString: String) = json.decodeFromString(WsCommand.serializer(), commandAsString)
}

@Serializable
sealed class WsCommand

@Serializable
class SubscribeOnUserEvents : WsCommand()

@Serializable
class UnsubscribeFromUserEvents :WsCommand()

@Serializable
class SubscribeOnGameStartedEvents: WsCommand()

@Serializable
class UnsubscribeFromGameStartedEvents: WsCommand()

@Serializable
class SubscribeOnGameEvents(val gameId: Int): WsCommand()

@Serializable
class UnsubscribeFromGameEvents(val gameId: Int): WsCommand()


@Serializable
sealed class BackendEvent

@Serializable
sealed class UserEvent: BackendEvent(){
    abstract val userId: Int
}

@Serializable
data class UserJoinedGame(override val userId: Int, val gameId: Int): UserEvent()

@Serializable
data class UserLeavedGame(override val userId: Int, val gameId: Int): UserEvent()



@Serializable
sealed class GameEvent: BackendEvent() {
    abstract val gameId: Int
}

@Serializable
data class GameStarted(override val gameId: Int, val userId: Int):GameEvent()

@Serializable
data class GameStateChanged(override val gameId: Int, val actualState: GameState):GameEvent()


@Serializable
data class UserJoined(override val gameId: Int, val user: UserInGame):GameEvent()


@Serializable
data class UserLeaved(override val gameId: Int, val user: UserInGame):GameEvent()


@Serializable
data class UserMoved(override val gameId: Int, val userId: Int, val time: Long, val x: Int, val y: Int):GameEvent()


@Serializable
data class UserWon(override val gameId: Int, val userId: Int, val winLine: Collection<Field>):GameEvent()

@Serializable
data class Draw(override val gameId: Int): GameEvent()

@Serializable
data class GameSnapshot(override val gameId: Int, val game: GameDto):GameEvent()
