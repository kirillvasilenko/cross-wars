package com.vkir.api

import com.vkir.model.UserDto
import io.ktor.client.request.get

class UsersApi{
    suspend fun getUser(userId: Int): UserDto {
        return client.get("$endpoint/api/users/$userId")
    }
}