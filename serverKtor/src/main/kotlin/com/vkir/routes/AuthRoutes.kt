package com.vkir.routes

import io.ktor.server.application.call
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
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