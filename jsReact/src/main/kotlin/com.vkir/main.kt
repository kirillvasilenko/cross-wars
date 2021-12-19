package com.vkir

import com.vkir.components.app
import com.vkir.viewModels.AppVm
import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import mu.KotlinLoggingConfiguration
import mu.KotlinLoggingLevel
import react.dom.render

val mainScope = MainScope()

fun main() {
    /*window.onload = {
        render(document.getElementById("root")) {
            child(Welcome::class) {
                attrs {
                    name = "Kotlin/JS"
                }
            }
        }
    }*/
    KotlinLoggingConfiguration.LOG_LEVEL = KotlinLoggingLevel.DEBUG
    render(document.getElementById("root")) {
        app{
            pVm = AppVm()
        }
    }
}
