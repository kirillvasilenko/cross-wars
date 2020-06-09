package model

import com.github.javafaker.Faker
import kotlinx.atomicfu.AtomicInt
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.write

class User(val id: Int, val name: String){

    private val lock: ReentrantReadWriteLock = ReentrantReadWriteLock()

    private var currentGame:Game? = null

    fun startNewGame(): Game{
        lock.write{
            checkCurrentGameIsNull("User must leave current game before starting new one.")
            val game = GamesStorage.makeGame(this)
            game.eventsListener = SubscriptionsHub::handleGameEvent
            currentGame = game
            return game
        }
    }

    fun joinGame(gameId: Int): Game{
        lock.write{
            if(currentGame?.id == gameId) return currentGame!!
            checkCurrentGameIsNull("User must leave current game before join another one.")

            val game = GamesStorage.getGame(gameId)
            game.join(this)
            return game
        }
    }

    fun leaveCurrentGame(){
        lock.write{
            if(currentGame == null) return
            currentGame!!.leave(this)
            currentGame = null
        }
    }

    fun makeMove(x: Int, y: Int){
        lock.write{
            checkCurrentGameIsNotNull("User must join a game for making move.")
            currentGame!!.makeMove(this, x, y)
        }
    }

    private fun checkCurrentGameIsNotNull(reason: String): Unit{
        if(currentGame == null) throw IncorrectUserActionException(reason)
    }

    private fun checkCurrentGameIsNull(reason: String): Unit{
        if(currentGame != null) throw IncorrectUserActionException(reason)
    }



}

open class UsersStorageInMemory{

    private val usersById = ConcurrentHashMap<Int, User>()
    private val idCounter = AtomicInteger()


    fun getUser(userId: Int): User =
        usersById[userId] ?: throw UserNotFoundException(userId)

    fun makeUser(): User {
        val user = User(idCounter.incrementAndGet(), generateUserName())
        usersById[user.id] = user
        return user
    }

    fun contains(userId: Int): Boolean = usersById.containsKey(userId)

}

object UsersStorage: UsersStorageInMemory()


fun generateUserName(): String{
    val faker = Faker()
    return faker.gameOfThrones().character()
}


class UserNotFoundException(userId: Int): UserFaultException("No user with id=$userId")

class IncorrectUserActionException(message: String): UserFaultException(message)