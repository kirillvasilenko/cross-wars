package model

open class UsersServiceInMemory{

    suspend fun getUser(userId: Int) = UsersStorage.getUser(userId).snapshot()
}

object UsersService: UsersServiceInMemory()