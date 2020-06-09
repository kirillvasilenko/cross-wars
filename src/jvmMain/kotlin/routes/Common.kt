package routes

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authentication
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.util.pipeline.PipelineContext
import io.ktor.websocket.WebSocketServerSession
import model.UserFaultException

fun PipelineContext<Unit, ApplicationCall>.getUserId(): Int {
    val idAsString = (call.authentication.principal as UserIdPrincipal).name
    return idAsString.toInt()
}

fun PipelineContext<Unit, ApplicationCall>.getIntFromParams(name: String) : Int =
    call.parameters[name]?.toIntOrNull()
        ?: throw UserFaultException("Missing or malformed $name")

fun PipelineContext<Unit, ApplicationCall>.getFromParams(name: String) : String =
    call.parameters[name]
        ?: throw UserFaultException("Missing param $name")

suspend fun PipelineContext<Unit, ApplicationCall>.badRequest(e: Exception){
    call.respondText(
        e.message!!,
        status = HttpStatusCode.BadRequest
    )
}

// web sockets

fun WebSocketServerSession.getUserId(): Int {
    val idAsString = (call.authentication.principal as UserIdPrincipal).name
    return idAsString.toInt()
}

suspend fun WebSocketServerSession.badRequest(e: Exception){
    call.respondText(
        e.message!!,
        status = HttpStatusCode.BadRequest
    )
}