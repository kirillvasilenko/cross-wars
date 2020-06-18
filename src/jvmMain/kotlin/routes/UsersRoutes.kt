package routes

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import model.UserFaultException
import app.UsersService

fun Route.getUser() {
    route("/users") {
        get("{id}") {
            try {
                val id = getIntFromParams("id")
                val user = UsersService.getUser(id)
                call.respond(user)
            }
            catch(e: UserFaultException){
                badRequest(e)
            }
        }
    }
}

fun Route.registerUsersRoutes() {
    getUser()
}