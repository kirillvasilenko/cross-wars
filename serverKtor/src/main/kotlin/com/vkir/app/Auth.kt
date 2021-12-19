package com.vkir.app

import com.vkir.model.SignUpData
import com.vkir.model.SubscriptionsHub
import com.vkir.model.UsersStorage
import mu.KotlinLogging
import org.slf4j.LoggerFactory
import java.util.*

private val log = KotlinLogging.logger {}

val workSession: UUID = UUID.randomUUID()

data class UserSession(val workSession: UUID, val userId: Int)

open class AuthServiceInMemory{

    private val log = LoggerFactory.getLogger(javaClass)

    fun signUpNewAnonymous(): UserSession {
        val user = UsersStorage.generateUser()
        return UserSession(workSession, user.id)
    }

    fun signUpNewUser(data: SignUpData): UserSession {
        val user = UsersStorage.makeUser(data.name, data.sideOfTheForce, data.swordColor)
        user.eventsListener = SubscriptionsHub::handleUserEvent
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