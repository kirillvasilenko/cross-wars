package model

import org.slf4j.LoggerFactory
import java.util.*

val workSession: UUID = UUID.randomUUID()

data class UserSession(val workSession: UUID, val userId: Int)

open class AuthServiceInMemory{

    private val log = LoggerFactory.getLogger(javaClass)

    fun authenticateNewUser(): UserSession{
        val user = UsersStorage.makeUser()
        return UserSession(workSession, user.id)
    }

    fun validate(session: UserSession): Boolean {
        log.debug("validating session: $session")
        val result = session.workSession == workSession
                && UsersStorage.contains(session.userId)
        log.debug("validation result: $result")
        return result
    }


}

object AuthService: AuthServiceInMemory()