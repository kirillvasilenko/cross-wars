package com.vkir.api.http

import com.vkir.model.UserDto
import io.ktor.client.*
import io.ktor.client.request.get

class AccountApi(private val client: HttpClient) {
    suspend fun getCurrentUser(): UserDto {
        return client.get("/api/account")
    }
}