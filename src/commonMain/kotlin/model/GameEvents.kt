package model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

object EventsSerializer{

    private val json = Json(JsonConfiguration.Stable)

    fun stringify(event: GameEvent) = json.stringify(GameEvent.serializer(), event)

    fun parse(eventAsString: String) = json.parse(GameEvent.serializer(), eventAsString)
}


@Serializable
sealed class GameEvent {
    abstract val gameId: Int
}

@Serializable
data class GameStarted(override val gameId: Int):GameEvent()

@Serializable
data class GameStateChanged(override val gameId: Int, val actualState: GameState):GameEvent()


@Serializable
data class UserJoined(override val gameId: Int, val userId: Int):GameEvent()


@Serializable
data class UserLeaved(override val gameId: Int, val userId: Int):GameEvent()


@Serializable
data class UserMoved(override val gameId: Int, val userId: Int, val time: Long, val x: Int, val y: Int):GameEvent()


@Serializable
data class UserWon(override val gameId: Int, val userId: Int, val winLine: Collection<Field>):GameEvent()


@Serializable
data class UserSubscribedOnGameEvents(override val gameId: Int, val userId: Int, val game: GameDto):GameEvent()
