package com.vkir.api.http

import com.vkir.model.SignUpData
import com.vkir.model.UserDto
import io.ktor.client.*
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthApi(private val client: HttpClient) {
    suspend fun signUpAnonymous(): UserDto {
        return client.post("/api/auth/sign-up/anonymous")
    }

    suspend fun signUp(data: SignUpData): UserDto {
        return client.post("/api/auth/sign-up"){
            contentType(ContentType.Application.Json)
            body = data
        }
    }

    suspend fun logout() {
        return client.post("/api/auth/logout")
    }
}