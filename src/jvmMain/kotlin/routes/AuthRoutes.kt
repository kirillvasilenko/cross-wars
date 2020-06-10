package routes

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.put
import io.ktor.routing.route
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import model.AuthService
import model.UserSession
import model.UsersService

fun Route.authRouting() {
    route("/auth") {
        put{
            var session: UserSession? = call.sessions.get<UserSession>()
            if(session == null
                || !AuthService.validate(session)){
                session = AuthService.authenticateNewUser()
                call.sessions.set(session)
            }
            val user = UsersService.getUser(session.userId)
            call.respond(user)
        }
    }
}

fun Route.registerAuthRoutes() {

    authRouting()
}