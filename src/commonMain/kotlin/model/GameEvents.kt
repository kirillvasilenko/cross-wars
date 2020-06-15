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
sealed class SpecificUserOnlyEvent(): GameEvent(){
    abstract val userId: Int
}

@Serializable
sealed class CertainGameEvent: GameEvent()

@Serializable
sealed class CommonGameEvent: CertainGameEvent()

@Serializable
data class GameStateChanged(override val gameId: Int, val actualState: GameState):
    CommonGameEvent()

@Serializable
data class UserJoined(override val gameId: Int, val userId: Int):
    CommonGameEvent()

@Serializable
data class UserLeaved(override val gameId: Int, val userId: Int):
    CommonGameEvent()

@Serializable
data class UserMoved(override val gameId: Int, val userId: Int, val time: Long, val x: Int, val y: Int):
    CertainGameEvent()

@Serializable
data class UserWon(override val gameId: Int, val userId: Int, val winLine: Collection<Field>):
    CertainGameEvent()

@Serializable
data class UserSubscribedOnGameEvents(override val gameId: Int, override val userId: Int, val game: GameDto):
    SpecificUserOnlyEvent()