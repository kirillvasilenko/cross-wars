package api

import io.ktor.client.request.get
import model.UserDto

class AccountApi{
    suspend fun getCurrentUser(): UserDto {
        return client.get("$endpoint/api/account")
    }
}