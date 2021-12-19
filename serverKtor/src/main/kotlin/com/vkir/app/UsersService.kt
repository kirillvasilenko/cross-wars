package com.vkir.app

import com.vkir.model.UsersStorage

open class UsersServiceInMemory{

    suspend fun getUser(userId: Int) = UsersStorage.getUser(userId).snapshot()
}

object UsersService: UsersServiceInMemory()