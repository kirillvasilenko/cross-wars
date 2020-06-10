package model

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import model.GameState.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.write
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.TimeMark


const val BOARD_SIZE = 10

const val WIN_LINE_LENGTH = 6

enum class GameState{
    CREATED, ACTIVE, COMPLETED, ARCHIVED
}


@Serializable
data class Field(val x: Int, val y: Int)

@Serializable
data class UserInGame(val id: Int, val symbol: Int)

@Serializable
data class GameDto(val id: Int)


class Game(val id: Int){

    private val mutex = Mutex()

    private var state: GameState

    private val users = mutableListOf<UserInGame>()

    private val board : Array<Array<UserInGame?>>

    private var lastMovedUserId = -1

    private var lastMovedTime: Long = 0

    private var lastUsedSymbol = -1

    var eventsListener: suspend (GameEvent) -> Unit = {}

    init{
        state = CREATED
        board = Array(BOARD_SIZE){
            Array<UserInGame?>(BOARD_SIZE) { null }
        }
    }

    //region public

    suspend fun start(user: User){
        mutex.withLock {
            if(state != CREATED) userFault(
                "Trying to start game that is in $state state. Only game is in $CREATED state can be started."
            )
            state = ACTIVE
            eventsListener(GameStateChanged(id, ACTIVE))
            joinImpl(user)
        }
    }

    suspend fun join(user: User){
        mutex.withLock {
            when(state){
                ACTIVE -> joinImpl(user)
                CREATED -> userFault(
                    "Trying to join to a inactive game. Join to an active game."
                )
                COMPLETED -> userFault(
                    "Trying to join to a completed game. Join to an active game."
                )
                ARCHIVED -> userFault(
                    "Trying to join to a archived game. Join to an active game."
                )
            }
        }
    }

    suspend fun leave(user: User){
        mutex.withLock{
            leaveImpl(user)
        }
    }

    suspend fun makeMove(user: User, x: Int, y: Int) {
        mutex.withLock{
            checkIfCanMove(user, x, y)
            makeMoveImpl(user, x, y)
        }
    }

    suspend fun subscribe(user: User) {
        mutex.withLock {
            if(!users.any { it.id == user.id }) userFault("Join to game $id for subscribing on its events.")
            subscribeImpl(user)
        }
    }

    suspend fun unsubscribe(user: User) {
        mutex.withLock {
            if(!users.any { it.id == user.id }) return
            unsubscribeImpl(user)
        }
    }

    suspend fun onSubscribed(userId: Int) {
        mutex.withLock{
            onSubscribeImpl(userId)
        }
    }

    suspend fun toDto(): GameDto {
        mutex.withLock {
            return toDtoImpl()
        }
    }

    //endregion public


    private suspend fun makeMoveImpl(user: User, x: Int, y: Int){
        if(board[x][y]?.id == user.id) return

        // make move
        val userInGame = users.first { it.id == user.id }
        board[x][y] = userInGame
        lastMovedUserId = user.id
        lastMovedTime = System.currentTimeMillis()
        eventsListener(UserMoved(id, user.id, lastMovedTime, x, y))

        // if user win, raise event
        val winLine = findWinLine(userInGame, x, y)
            ?: return
        eventsListener(UserWon(id, user.id, winLine))

        // and complete game
        state = COMPLETED
        eventsListener(GameStateChanged(id, COMPLETED))
    }

    private fun findWinLine(userInGame: UserInGame, x: Int, y:Int): Collection<Field>?{
        // vertical
        val result = mutableListOf<Field>()
        for(i in 0 until BOARD_SIZE){
            if(board[i][y] == userInGame)
                result.add(Field(i, y))
            else
                result.clear()
            if(result.size == WIN_LINE_LENGTH)
                return result
        }

        // horizontal
        result.clear()
        for(j in 0 until BOARD_SIZE){
            if(board[x][j] == userInGame)
                result.add(Field(x, j))
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
                result.add(Field(i, j))
            else
                result.clear()
            if(result.size == WIN_LINE_LENGTH)
                return result
            i++;j++
        }

        // diagonal right to left
        result.clear()
        stepsToStartPosition = min(x, BOARD_SIZE - y - 1)
        i = x - stepsToStartPosition
        j = y + stepsToStartPosition
        while(i < BOARD_SIZE && j >= 0){
            if(board[i][j] == userInGame)
                result.add(Field(i, j))
            else
                result.clear()
            if(result.size == WIN_LINE_LENGTH)
                return result
            i++;j--
        }

        return null
    }

    private fun checkIfCanMove(user: User, x: Int, y: Int){
        if(!users.any { it.id == user.id }) userFault("Join to game $id for making move.")
        if(state != ACTIVE) userFault(
            "Trying to make move in $state game."
        )
        if(lastMovedUserId == user.id) userFault("Trying to make move several times in a row. Wait your turn.")
        if(x !in 0 until BOARD_SIZE || y !in 0 until BOARD_SIZE) userFault(
            "x=$x or y=$y are out of board. Board size=$BOARD_SIZE."
        )
        if(board[x][y] != null && board[x][y]!!.id != user.id)
            userFault("Field ($x,$y) has already been occupied. Try to move to another field.")
    }


    private suspend fun joinImpl(user: User){
        lastUsedSymbol++
        users.add(UserInGame(user.id, lastUsedSymbol))
        eventsListener(UserJoined(id, user.id))
        subscribeImpl(user)
    }

    private suspend fun leaveImpl(user: User){
        if(!users.removeIf { it.id == user.id }){
            return
        }
        eventsListener(UserLeaved(id, user.id))
        unsubscribeImpl(user)
        if(users.isEmpty()){
            archive()
        }
    }

    private suspend fun subscribeImpl(user: User){
        SubscriptionsHub.subscribeOnGameEvents(user.id, id)
        onSubscribeImpl(user.id)
    }

    private suspend fun onSubscribeImpl(userId: Int){
        eventsListener(UserSubscribedOnGameEvents(id, userId, toDtoImpl()))
    }

    private fun unsubscribeImpl(user: User){
        SubscriptionsHub.unsubscribeFromGameEvents(user.id, id)
    }

    private suspend fun archive(){
        state = ARCHIVED
        eventsListener(GameStateChanged(id, ARCHIVED))
        eventsListener = {}
    }

    private fun toDtoImpl() = GameDto(id)

}