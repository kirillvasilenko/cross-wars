package com.vkir.api

import com.vkir.utils.generateGuid
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun createJson() = Json {
    isLenient = true
    ignoreUnknownKeys = true
}

fun createHttpClient(
    defaultHost: String,
    defaultPort: Int,
    defaultProtocol: URLProtocol,
    json: Json,
    enableNetworkLogs: Boolean = false
) = HttpClient {
    followRedirects = true
    expectSuccess = true
    install(WebSockets) {
        // Configure WebSockets
    }
    install(JsonFeature) {
        serializer = KotlinxSerializer(json)
    }
    defaultRequest {
        this.url {
            // it breaks ws connection, the client sends a request on with http schema when trying to set up a ws connection
            // looks like a bug
            //protocol = defaultProtocol
        }
        host = defaultHost
        port = defaultPort
        contentType(ContentType.Application.Json)
        header(HttpHeaders.XRequestId, generateGuid())
    }
    HttpResponseValidator {
        validateResponse { response ->
            when (response.status) {
                HttpStatusCode.OK -> Unit
                HttpStatusCode.BadRequest -> {
                    val error = response.receive<ApiErrorDto>()
                    error.raise()
                }
                HttpStatusCode.InternalServerError -> {
                    val error = response.receive<ApiErrorDto>()
                    error.raise()
                }
                else -> {
                    throw AppFaultException(
                        message = response.receive(),
                        errorCode = response.status.value
                    )
                }
            }
        }
    }
    if (enableNetworkLogs) {
        install(Logging)
    }
}