package model

import java.util.*

val workSession: UUID = UUID.randomUUID()

data class UserSession(val workSession: UUID, val userId: Int)

open class AuthServiceInMemory{
    fun authenticateNewUser(): UserSession{
        val user = UsersStorage.makeUser()
        return UserSession(workSession, user.id)
    }

    fun validate(session: UserSession): Boolean =
        session.workSession == workSession
                && UsersStorage.contains(session.userId)

}

object AuthService: AuthServiceInMemory()