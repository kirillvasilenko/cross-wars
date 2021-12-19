package com.vkir.routes

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import com.vkir.app.UsersService

fun Route.accountRouting() {
    route("/account") {
        get{
            val user = UsersService.getUser(getUserId())
            call.respond(user)
        }
    }
}

fun Route.registerAccountRoutes() {
    accountRouting()
}