package com.vkir.routes

import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import com.vkir.app.UsersService

fun Route.accountRouting() {
    route("/account") {
        get {
            val user = UsersService.getUser(getUserId())
            call.respond(user)
        }
    }
}

fun Route.registerAccountRoutes() {
    accountRouting()
}