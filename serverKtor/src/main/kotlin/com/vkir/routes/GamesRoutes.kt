package com.vkir.routes

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.*
import com.vkir.app.GamesService
import com.vkir.model.UserFaultException

fun Route.gettingGames() {
    route("/games") {
        get{
            call.respond(GamesService.getGames())
        }
        get("{id}") {
            try {
                val id = getIntFromParams("id")
                val game = GamesService.getGame(id)
                call.respond(game)
            }
            catch(e: UserFaultException){
                badRequest(e)
            }
        }
    }
}

fun Route.startNewGame() {
    route("/games/start-new") {
        post {
            try {
                val newGame = GamesService.startNewGame(getUserId())
                call.respond(newGame)
            }
            catch(e: UserFaultException){
                badRequest(e)
            }
        }
    }
}

fun Route.joinGame() {
    route("/games/{id}/join") {
        put {
            try {
                val game = GamesService.joinGame(getUserId(), getIntFromParams("id"))
                call.respond(game)
            }
            catch(e: UserFaultException){
                badRequest(e)
            }
        }
    }
}

fun Route.leaveCurrentGame() {
    route("/games/leave") {
        put {
            try {
                GamesService.leaveCurrentGame(getUserId())
                call.respond("Success")
            }
            catch(e: UserFaultException){
                badRequest(e)
            }
        }
    }
}

fun Route.makeMove(){
    route("/games/move"){
        put{
            try {
                val x = getIntFromParams("x")
                val y = getIntFromParams("y")
                GamesService.makeMove(getUserId(), x, y)
                call.respond("Success")
            }
            catch(e: UserFaultException){
                badRequest(e)
            }
        }
    }
}

fun Route.registerGamesRoutes() {
    gettingGames()
    startNewGame()
    joinGame()
    leaveCurrentGame()
    makeMove()
}