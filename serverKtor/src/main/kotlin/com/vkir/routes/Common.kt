package com.vkir.routes

import com.vkir.model.UserFaultException
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authentication
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.util.pipeline.PipelineContext
import io.ktor.websocket.*


fun PipelineContext<Unit, ApplicationCall>.getUserId(): Int {
    val idAsString = (call.authentication.principal as UserIdPrincipal).name
    return idAsString.toInt()
}

fun PipelineContext<Unit, ApplicationCall>.getIntFromParams(name: String) : Int =
    call.parameters[name]?.toIntOrNull()
        ?: throw UserFaultException("Missing or malformed $name")

suspend fun PipelineContext<Unit, ApplicationCall>.badRequest(e: Throwable){
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
