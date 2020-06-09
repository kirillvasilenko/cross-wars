package routes

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import model.AuthService
import model.UserSession

fun Route.authRouting() {
    route("/auth") {
        put{
            var session: UserSession? = call.sessions.get<UserSession>()
            if(session == null
                || !AuthService.validate(session)){
                session = AuthService.authenticateNewUser()
                call.sessions.set(session)
                call.respond("New session set!")
            }
            else{
                call.respond("Old session will remain.")
            }
        }
        // todo delete
        authenticate{
            get{
                val session: UserSession? = call.sessions.get<UserSession>()
                if(session == null){
                    call.respond("Empty session!")
                }
                else{
                    call.respond("${session.userId}")
                }
            }
        }
    }
}

fun Route.registerAuthRoutes() {

    authRouting()
}