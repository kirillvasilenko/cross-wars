package api

import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import model.SignUpData
import model.UserDto

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
}