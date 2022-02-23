package com.vkir

import com.vkir.api.CommonError
import com.vkir.api.createHttpClient
import com.vkir.api.createJson
import com.vkir.api.http.Api
import io.ktor.http.*
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initObjects() {
    // Errors, we have to touch static classes to initialize their properties
    CommonError
}

fun commonClientModule(
    host: String,
    port: Int,
    protocol: URLProtocol,
    enableNetworkLogs: Boolean
) = module {
    single { createJson() }
    single { createHttpClient(
        defaultHost = host,
        defaultPort = port,
        defaultProtocol = protocol,
        json = get(),
        enableNetworkLogs = enableNetworkLogs
    ) }

    single { Api(get()) }
}

fun initClientKoin(
    host: String,
    port: Int,
    protocol: URLProtocol,
    enableNetworkLogs: Boolean,
    appDeclaration: KoinAppDeclaration = {}
) {
    initObjects()
    startKoin {
        appDeclaration()
        modules(commonClientModule(
            host = host,
            port = port,
            protocol = protocol,
            enableNetworkLogs = enableNetworkLogs
        ))
    }
}