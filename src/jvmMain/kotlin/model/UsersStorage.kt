package model

import com.github.javafaker.Faker
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

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