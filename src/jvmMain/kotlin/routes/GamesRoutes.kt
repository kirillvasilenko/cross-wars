package routes

import io.ktor.application.call
import io.ktor.response.respond
import model.GamesService
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import model.UserFaultException


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
        post {
            try {
                val newGame = GamesService.joinGame(getUserId(), getIntFromParams("id"))
                call.respond(newGame)
            }
            catch(e: UserFaultException){
                badRequest(e)
            }
        }
    }
}

fun Route.leaveGame() {
    route("/games/leave") {
        post {
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

fun Route.registerGamesRoutes() {
    gettingGames()
    startNewGame()
    joinGame()
    leaveGame()
}