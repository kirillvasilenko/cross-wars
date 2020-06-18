package routes

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
import app.AuthService
import model.SignUpData
import app.UserSession
import app.UsersService

fun Route.authRouting() {
    route("/auth/sign-up/anonymous") {
        post{
            var session: UserSession? = call.sessions.get<UserSession>()
            if(session == null
                || !AuthService.validate(session)){
                session = AuthService.signUpNewAnonymous()
                call.sessions.set(session)
            }
            val user = UsersService.getUser(session.userId)
            call.respond(user)
        }
    }

    route("/auth/sign-up") {
        post{
            try{
                val signUpData = call.receive<SignUpData>()
                val session = AuthService.signUpNewUser(signUpData)
                call.sessions.set(session)
                val user = UsersService.getUser(session.userId)
                call.respond(user)
            }
            catch(e:ContentTransformationException){
                badRequest(e)
            }
        }
    }
}

fun Route.registerAuthRoutes() {
    authRouting()
}