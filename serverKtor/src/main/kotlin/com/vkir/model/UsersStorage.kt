package com.vkir.model

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

open class UsersStorageInMemory{

    private val usersById = ConcurrentHashMap<Int, User>()

    private val idCounter = AtomicInteger()

    fun getUser(userId: Int): User =
        usersById[userId] ?: throw UserNotFoundException(userId)

    fun generateUser(): User {
        val user = generateUser(idCounter.incrementAndGet())
        usersById[user.id] = user
        return user
    }

    fun makeUser(name: String, sideOfTheForce: SideOfTheForce, swordColor: Int): User{
        val user = User(idCounter.incrementAndGet(), name, sideOfTheForce, swordColor)
        usersById[user.id] = user
        return user
    }

    fun contains(userId: Int): Boolean = usersById.containsKey(userId)
}

object UsersStorage: UsersStorageInMemory()

val namesOfHeroes = mutableListOf(
        "Java the Hutt",
        "Jav Jav Scripts",
        "Oba-Dva Kotlin",
        "C-3++",
        "Princess Lua",
        "Luke Phywalker",
        "Dart Googler",
        "Boba Perl",
        "Forthan Solo",
        "R-da-D-net",
        "YAda",
        "Rubacca")

fun generateUser(id: Int): User{
    val name =
            if (namesOfHeroes.size > 0)
                namesOfHeroes.removeAt(Random.nextInt(namesOfHeroes.size))
            else "R${Random.nextInt(100)}-D${Random.nextInt(100)}"
    val side =
            if(Random.nextInt(2) == 0) SideOfTheForce.Light
            else SideOfTheForce.Dark
    val swordColor = Random.nextInt(3)
    return User(id, name, side, swordColor)
}

class UserNotFoundException(userId: Int): UserFaultException("No user with id=$userId")