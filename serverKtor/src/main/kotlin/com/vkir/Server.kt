package com.vkir

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.session
import io.ktor.http.HttpStatusCode
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import kotlinx.coroutines.launch
import com.vkir.app.AuthService
import com.vkir.app.UserSession
import com.vkir.routes.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.websocket.*
import io.ktor.server.websocket.WebSockets
import io.ktor.server.response.*
import io.ktor.server.routing.*
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
    val testPlayersEnabled = environment.config.propertyOrNull("testPlayers.enabled")?.getString()?.toBoolean() ?: false
    if (testPlayersEnabled) {
        launch {
            runTestPlayers()
        }
    }
}







