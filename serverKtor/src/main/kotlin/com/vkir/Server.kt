package com.vkir

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.session
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.pingPeriod
import io.ktor.http.cio.websocket.timeout
import io.ktor.serialization.json
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.websocket.WebSockets
import kotlinx.coroutines.launch
import com.vkir.app.AuthService
import com.vkir.app.UserSession
import com.vkir.routes.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import mu.KotlinLogging
import java.time.Duration


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

private val log = KotlinLogging.logger {}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    install(CallLogging)
    // todo configure CORS on prod (just to understand how it works)
    // maybe the server should "know" that it works on the prod's domain
    install(Compression) {
        gzip()
    }
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(5)
        timeout = Duration.ofSeconds(3)
        maxFrameSize = Long.MAX_VALUE
    }
    install(Sessions) {
        cookie<UserSession>("USER_SESSION")
    }
    install(Authentication) {
        session<UserSession> {
            validate { userSession ->
                log.info { "trying to validate userSession" }
                if (AuthService.validate(userSession)) {
                    UserIdPrincipal(userSession.userId.toString())
                } else {
                    null
                }
            }
            challenge {
                log.info { "in the challenge section" }
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }

    routing {
        registerStaticRoutes()

        // it needs to enable handling CORS preflight requests from browsers https://youtrack.jetbrains.com/issue/KTOR-3255
        options { }

        route("api") {
            registerAuthRoutes()

            authenticate {
                registerAccountRoutes()
                registerWsConnections()
                registerGamesRoutes()
                registerUsersRoutes()
            }
        }
    }
    launch {
        runTestPlayers()
    }
}







