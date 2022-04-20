package com.vkir.routes

import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import com.vkir.app.UsersService
import com.vkir.model.UserFaultException

fun Route.getUser() {
    route("/users") {
        get("{id}") {
            try {
                val id = getIntFromParams("id")
                val user = UsersService.getUser(id)
                call.respond(user)
            } catch (e: UserFaultException) {
                badRequest(e)
            }
        }
    }
}

fun Route.registerUsersRoutes() {
    getUser()
}