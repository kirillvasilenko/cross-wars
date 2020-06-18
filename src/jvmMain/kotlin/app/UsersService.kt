package app

import model.UsersStorage

open class UsersServiceInMemory{

    suspend fun getUser(userId: Int) = UsersStorage.getUser(userId).snapshot()
}

object UsersService: UsersServiceInMemory()