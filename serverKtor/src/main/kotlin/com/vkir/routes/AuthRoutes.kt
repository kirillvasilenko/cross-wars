package com.vkir.routes

import io.ktor.application.call
import io.ktor.request.ContentTransformationException
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import com.vkir.app.AuthService
import com.vkir.app.UserSession
import com.vkir.app.UsersService
import com.vkir.model.SignUpData
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

fun Route.authRouting() {

    route("/auth/sign-up/anonymous") {
        post {
            var session: UserSession? = call.sessions.get<UserSession>()
            if (session == null
                || !AuthService.validate(session)
            ) {
                session = AuthService.signUpNewAnonymous()
                call.sessions.set(session)
            }
            val user = UsersService.getUser(session.userId)
            call.respond(user)
        }
    }

    route("/auth/sign-up") {
        post {
            try {
                log.info { "we are in the sing-up method" }
                val signUpData = call.receive<SignUpData>()
                val session = AuthService.signUpNewUser(signUpData)
                call.sessions.set(session)
                val user = UsersService.getUser(session.userId)
                call.respond(user)
                log.debug("$signUpData")
            } catch (e: ContentTransformationException) {
                badRequest(e)
            }
        }
    }

    route("/auth/logout") {
        post {
            try {
                call.sessions.clear("USER_SESSION")
                call.respond("Success")
            } catch (e: ContentTransformationException) {
                badRequest(e)
            }
        }
    }
}

fun Route.registerAuthRoutes() {
    log.info { "registerAuthRoutes" }
    authRouting()
}