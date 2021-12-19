package com.vkir.api

import com.vkir.model.UserDto
import io.ktor.client.request.get

class AccountApi{
    suspend fun getCurrentUser(): UserDto {
        return client.get("$endpoint/api/account")
    }
}