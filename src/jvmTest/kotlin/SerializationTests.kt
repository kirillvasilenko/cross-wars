import model.*
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class SerializationTests {

    @Test
    fun backendEventsSmokeTest() {

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

    @Test
    fun wsCommandsSmokeTest() {

        val subscribeOnUserEvents = SubscribeOnUserEvents()
        val unsubscribeFromUserEvents = UnsubscribeFromUserEvents()

        val jsonSubscribeOnUserEvents = WsCommandsSerializer.stringify(subscribeOnUserEvents)
        val jsonUnsubscribeFromUserEvents = WsCommandsSerializer.stringify(unsubscribeFromUserEvents)

        val parsedSubscribeOnUserEvents = WsCommandsSerializer.parse(jsonSubscribeOnUserEvents)
        val parsedUnsubscribeFromUserEvents = WsCommandsSerializer.parse(jsonUnsubscribeFromUserEvents)

        assertTrue(parsedSubscribeOnUserEvents is SubscribeOnUserEvents)
        assertTrue(parsedUnsubscribeFromUserEvents is UnsubscribeFromUserEvents)
    }
}