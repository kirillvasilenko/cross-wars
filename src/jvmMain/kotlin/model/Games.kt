package model

import kotlinx.serialization.Serializable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.write
import kotlin.math.min

enum class GameState{
    ACTIVE, COMPLETED, ARCHIVED
}

@Serializable
sealed class GameEvent(val gameId: Int)

class GameStateChanged(gameId: Int, val actualState: GameState):GameEvent(gameId)

class UserJoined(gameId: Int, val userId: Int) : GameEvent(gameId)

class UserLeaved(gameId: Int, val userId: Int) : GameEvent(gameId)

class UserMoved(gameId: Int, val userId: Int, val x: Int, val y: Int): GameEvent(gameId)

class UserWon(gameId: Int, val userId: Int, val winLine: Collection<Field>): GameEvent(gameId)

const val BOARD_SIZE = 10

const val WIN_LINE_LENGTH = 6

data class Field(val x: Int, val y: Int)

class Game(val id: Int, creator: User){

    private val lock: ReentrantReadWriteLock = ReentrantReadWriteLock()

    private var state: GameState

    private val users = mutableListOf<UserInGame>()

    private val board : Array<Array<UserInGame?>>

    private var lastMovedUserId = -1

    private var lastUsedSymbol = -1

    var eventsListener: (GameEvent) -> Unit = {}

    init{
        state = GameState.ACTIVE
        joinImpl(creator)
        board = Array(BOARD_SIZE){
            Array(BOARD_SIZE) { null }
        }
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

    fun makeMove(user: User, x: Int, y: Int) {
        lock.write{
            checkIfCanMove(user, x, y)
            makeMoveImpl(user, x, y)
        }
    }

    private fun makeMoveImpl(user: User, x: Int, y: Int){
        if(board[x][y]?.id == user.id) return

        // make move
        val userInGame = users.first { it.id == user.id }
        board[x][y] = userInGame
        lastMovedUserId = user.id
        eventsListener(UserMoved(id, user.id, x, y))

        // if user win, raise event
        val winLine = findWinLine(userInGame, x, y)
            ?: return
        eventsListener(UserWon(id, user.id, winLine))

        // and complete game
        state = GameState.COMPLETED
        eventsListener(GameStateChanged(id, GameState.COMPLETED))
    }

    private fun findWinLine(userInGame: UserInGame, x: Int, y:Int): Collection<Field>?{
        // vertical
        val result = mutableListOf<Field>()
        for(i in 0 until BOARD_SIZE){
            if(board[i][y] == userInGame)
                result.add(Field(i,y))
            else
                result.clear()
            if(result.size == WIN_LINE_LENGTH)
                return result
        }

        // horizontal
        result.clear()
        for(j in 0 until BOARD_SIZE){
            if(board[x][j] == userInGame)
                result.add(Field(x,j))
            else
                result.clear()
            if(result.size == WIN_LINE_LENGTH)
                return result
        }

        // diagonal left to right
        result.clear()
        var stepsToStartPosition = min(x, y)
        var i = x - stepsToStartPosition
        var j = y - stepsToStartPosition
        while(i < BOARD_SIZE && j < BOARD_SIZE){
            if(board[i][j] == userInGame)
                result.add(Field(i,j))
            else
                result.clear()
            if(result.size == WIN_LINE_LENGTH)
                return result
            i++;j++
        }

        // diagonal right to left
        result.clear()
        stepsToStartPosition = min(x,BOARD_SIZE - y - 1)
        i = x - stepsToStartPosition
        j = y + stepsToStartPosition
        while(i < BOARD_SIZE && j >= 0){
            if(board[i][j] == userInGame)
                result.add(Field(i,j))
            else
                result.clear()
            if(result.size == WIN_LINE_LENGTH)
                return result
            i++;j--
        }

        return null
    }

    private fun checkIfCanMove(user: User, x: Int, y: Int){
        if(!users.any { it.id == user.id }) throw IncorrectUserActionException("Join to game $id for making move.")
        if(state != GameState.ACTIVE) throw IncorrectUserActionException("Trying to make move in $state game.")
        if(lastMovedUserId == user.id) throw IncorrectUserActionException("Trying to make move several times in a row. Wait your turn.")
        if(x !in 0 until BOARD_SIZE || y !in 0 until BOARD_SIZE) throw IncorrectUserActionException("x=$x or y=$y are out of board. Board size=$BOARD_SIZE.")
        if(board[x][y] != null && board[x][y]!!.id != user.id)
            throw IncorrectUserActionException("Field ($x,$y) has already been occupied. Try to move to another field.")
    }


    private fun joinImpl(user: User){
        lastUsedSymbol++
        users.add(UserInGame(user.id, lastUsedSymbol))
        eventsListener(UserJoined(id, user.id))
    }

    private fun leaveImpl(user: User){
        if(!users.removeIf { it.id == user.id }){
            return
        }
        eventsListener(UserLeaved(id, user.id))
        if(users.isEmpty()){
            archive()
        }
    }

    private fun archive(){
        state = GameState.ARCHIVED
        eventsListener(GameStateChanged(id, GameState.ARCHIVED))
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

    fun makeMove(userId: Int, x: Int, y: Int) {
        val user = UsersStorage.getUser(userId)
        user.makeMove(x, y)
    }

}

object GamesService:GamesServiceInMemory()

class GameNotFoundException(gameId: Int): UserFaultException("No game with id=$gameId")