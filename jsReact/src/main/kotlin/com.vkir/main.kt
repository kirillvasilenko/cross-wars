package com.vkir

import com.vkir.api.Api
import com.vkir.components.App
import com.vkir.components.app
import com.vkir.viewModels.AppVm
import io.ktor.client.*
import io.ktor.http.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import mu.KotlinLoggingConfiguration
import mu.KotlinLoggingLevel
import org.koin.core.Koin
import org.koin.core.context.KoinContext
import org.koin.mp.KoinPlatformTools
import react.dom.render

val mainScope = MainScope()

val https = window.location.protocol.contains("https")
val port = window.location.port.toIntOrNull()
    ?: if (https) 433 else 80

fun main() {
    KotlinLoggingConfiguration.LOG_LEVEL = KotlinLoggingLevel.DEBUG
    initClientKoin(
        host = window.location.hostname,
        protocol = if (https) URLProtocol.HTTPS else URLProtocol.HTTP,
        port = port,
        enableNetworkLogs = true
    )

    Api = KoinPlatformTools.defaultContext().get().get()

    window.onload = {
        render(document.getElementById("root")!!) {
            app{
                pVm = AppVm()
            }
        }
    }
}
