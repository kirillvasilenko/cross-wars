package com.vkir.api.http

import com.vkir.model.UserDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.get

class UsersApi(private val client: HttpClient) {
    suspend fun getUser(userId: Int): UserDto {
        return client.get("/api/users/$userId").body()
    }
}