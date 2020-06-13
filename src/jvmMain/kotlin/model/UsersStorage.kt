package model

import com.github.javafaker.Faker
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

open class UsersStorageInMemory{

    private val usersById = ConcurrentHashMap<Int, User>()

    private val idCounter = AtomicInteger()

    fun getUser(userId: Int): User =
        usersById[userId] ?: throw UserNotFoundException(userId)

    fun makeUser(): User {
        val user = generateUser(idCounter.incrementAndGet())
        usersById[user.id] = user
        return user
    }

    fun contains(userId: Int): Boolean = usersById.containsKey(userId)
}

object UsersStorage: UsersStorageInMemory()

fun generateUser(id: Int): User{
    val faker = Faker()
    val name = faker.gameOfThrones().character()
    val side =
            if(Random.nextInt(2) == 0) SideOfTheForce.Light
            else SideOfTheForce.Dark
    val swordColor = Random.nextInt(3)
    return User(id, name, side, swordColor)
}

class UserNotFoundException(userId: Int): UserFaultException("No user with id=$userId")