import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.respond
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.sessions.*
import io.ktor.websocket.*
import model.AuthService
import model.UserSession
import routes.*


fun main() {
    embeddedServer(Netty, 9090) {
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            method(HttpMethod.Get)
            method(HttpMethod.Post)
            method(HttpMethod.Put)
            method(HttpMethod.Delete)
            anyHost()
        }
        install(Compression) {
            gzip()
        }
        install(WebSockets)
        install(Sessions) {
            cookie<UserSession>("USER_SESSION")
        }
        install(Authentication) {
            session<UserSession>{
                validate { userSession ->
                    if(AuthService.validate(userSession)){
                        UserIdPrincipal(userSession.userId.toString())
                    }
                    else{
                        null
                    }
                }
                challenge {
                    call.respond(HttpStatusCode.Unauthorized)
                }
            }
        }

        routing {
            registerStaticRoutes()

            route("api"){
                registerAuthRoutes()

                authenticate{
                    registerSubscriptions()
                    registerGamesRoutes()
                    registerUsersRoutes()
                }
            }
        }

    }.start(wait = true)
}

