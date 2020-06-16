package routes

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.put
import io.ktor.routing.route
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import log
import model.AuthService
import model.UserSession
import model.UsersService

fun Route.authRouting() {
    route("/auth") {
        put{
            var session: UserSession? = call.sessions.get<UserSession>()
            if(session == null
                || !AuthService.validate(session)){
                log.debug("need to auth new user")
                session = AuthService.authenticateNewUser()
                log.debug("new session:$session")
                call.sessions.set(session)
                log.debug("session set to cookie")
            }
            val user = UsersService.getUser(session.userId)
            call.respond(user)
        }
    }
}

fun Route.registerAuthRoutes() {

    authRouting()
}