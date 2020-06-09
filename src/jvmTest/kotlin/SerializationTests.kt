import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import model.GameEvent
import model.GameState
import model.GameStateChanged
import model.UserJoined
import org.junit.Test
import kotlin.test.assertEquals


class SerializationTests {
    @Test
    fun smokeTest() {

        val json = Json(JsonConfiguration.Stable)

        val stateChanged = GameStateChanged(1, GameState.ACTIVE)
        val userJoined = UserJoined(2, 3)

        val jsonStateChanged = json.stringify(GameEvent.serializer(), stateChanged)
        val jsonUserJoined = json.stringify(GameEvent.serializer(), userJoined)

        val parsedStateChanged = json.parse(GameEvent.serializer(), jsonStateChanged)
        val parsedUserJoined = json.parse(GameEvent.serializer(), jsonUserJoined)

        assertEquals(stateChanged, parsedStateChanged)
        assertEquals(userJoined, parsedUserJoined)
    }
}