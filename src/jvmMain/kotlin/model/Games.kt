package model

import kotlinx.serialization.Serializable
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.write

enum class GameState{
    ACTIVE, COMPLETED, ARCHIVED
}

@Serializable
sealed class GameEvent(val gameId: Int)

class UserJoined(gameId: Int, val userId: Int) : GameEvent(gameId)

class UserLeaved(gameId: Int, val userId: Int) : GameEvent(gameId)

class Game(val id: Int, creator: User){

    private val lock: ReentrantReadWriteLock = ReentrantReadWriteLock()

    private var state: GameState

    private val users = mutableListOf<UserInGame>()

    private var lastUsedSymbol = 0

    var eventsListener: (GameEvent) -> Unit = {}

    init{
        state = GameState.ACTIVE
        joinImpl(creator)
    }

    fun join(user: User){
        lock.write {
            when(state){
                GameState.ACTIVE -> joinImpl(user)
                GameState.COMPLETED -> throw IncorrectUserActionException("Trying to join to a completed game. Join to an active game.")
                GameState.ARCHIVED -> throw IncorrectUserActionException("Trying to join to a archived game. Join to an active game.")
            }
        }
    }

    fun leave(user: User){
        lock.write{
            leaveImpl(user)
        }
    }

    private fun joinImpl(user: User){
        lastUsedSymbol++
        users.add(UserInGame(user.id, lastUsedSymbol))
        eventsListener(UserJoined(id, user.id))
    }

    private fun leaveImpl(user: User){
        if(!users.removeIf { userInGame -> userInGame.id == user.id }){
            return
        }
        eventsListener(UserLeaved(id, user.id))
        if(users.isEmpty()){
            archive()
        }
    }

    private fun archive(){
        state = GameState.ARCHIVED
        eventsListener = {}
    }

    fun toDto(): GameDto{
        return GameDto(id)
    }

}

data class UserInGame(val id: Int, val symbol: Int)


@Serializable
data class GameDto(val id: Int)

open class GamesStorageInMemory{

    private val idCounter = AtomicInteger()
    private val gamesById = ConcurrentHashMap<Int, Game>()

    fun getGame(id: Int): Game
            = gamesById[id] ?: throw GameNotFoundException(id)

    fun getGames() : Collection<Game> {
        return gamesById.values
    }

    fun makeGame(creator:User): Game {
        val id = idCounter.incrementAndGet()
        val game = Game(id, creator)
        gamesById[id] = game
        return game
    }

}

object GamesStorage: GamesStorageInMemory()

open class GamesServiceInMemory{

    fun getGames(): Collection<GameDto>{
        return GamesStorage.getGames().map{ it.toDto() }
    }

    fun getGame(id: Int): GameDto
            = GamesStorage.getGame(id).toDto()

    fun startNewGame(userId: Int): GameDto{
        val user = UsersStorage.getUser(userId)
        return user.startNewGame().toDto()
    }

    fun joinGame(userId: Int, gameId: Int): GameDto{
        val user = UsersStorage.getUser(userId)
        return user.joinGame(gameId).toDto()
    }

    fun leaveCurrentGame(userId: Int): Unit{
        val user = UsersStorage.getUser(userId)
        user.leaveCurrentGame()
    }
}

object GamesService:GamesServiceInMemory()

class GameNotFoundException(gameId: Int): UserFaultException("No game with id=$gameId")