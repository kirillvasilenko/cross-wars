package api

import io.ktor.client.request.get
import model.UserDto

class UsersApi{
    suspend fun getUser(userId: Int): UserDto {
        return client.get("$endpoint/api/users/$userId")
    }
}