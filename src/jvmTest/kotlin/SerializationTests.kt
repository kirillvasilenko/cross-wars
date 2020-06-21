import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import model.*
import org.junit.Test
import kotlin.test.assertEquals


class SerializationTests {
    @Test
    fun smokeTest() {

        val stateChanged = GameStateChanged(1, GameState.ACTIVE)
        val userJoined = UserJoined(2, UserInGame(3, 2, true, "Name", SideOfTheForce.Dark, 3))
        val userJoinedGame = UserJoinedGame(1, 3)

        val jsonStateChanged = EventsSerializer.stringify(stateChanged)
        val jsonUserJoined = EventsSerializer.stringify(userJoined)
        val jsonUserJoinedGame = EventsSerializer.stringify(userJoinedGame)

        val parsedStateChanged = EventsSerializer.parse(jsonStateChanged)
        val parsedUserJoined = EventsSerializer.parse(jsonUserJoined)
        val parsedUserJoinedGame = EventsSerializer.parse(jsonUserJoinedGame)

        assertEquals(stateChanged, parsedStateChanged)
        assertEquals(userJoined, parsedUserJoined)
        assertEquals(userJoinedGame, parsedUserJoinedGame)
    }
}