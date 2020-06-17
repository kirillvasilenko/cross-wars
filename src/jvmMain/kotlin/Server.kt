import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.session
import io.ktor.features.CORS
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.gzip
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.pingPeriod
import io.ktor.http.cio.websocket.timeout
import io.ktor.response.respond
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.websocket.WebSockets
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import model.AuthService
import model.UserSession
import model.UsersStorage
import routes.*
import java.time.Duration


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
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
    install(WebSockets){
        pingPeriod = Duration.ofSeconds(5)
        timeout = Duration.ofSeconds(3)
        maxFrameSize = Long.MAX_VALUE
    }
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
                registerAccountRoutes()
                registerSubscriptions()
                registerGamesRoutes()
                registerUsersRoutes()
            }
        }
    }
    launch{
        runTestPlayers()
    }
}







