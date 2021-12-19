package com.vkir.api

import com.vkir.model.SignUpData
import com.vkir.model.UserDto
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthApi{
    suspend fun signUpAnonymous(): UserDto {
        return client.post("$endpoint/api/auth/sign-up/anonymous")
    }

    suspend fun signUp(data: SignUpData): UserDto {
        return client.post("$endpoint/api/auth/sign-up"){
            contentType(ContentType.Application.Json)
            body = data
        }
    }

    suspend fun logout() {
        return client.post("$endpoint/api/auth/logout")
    }
}