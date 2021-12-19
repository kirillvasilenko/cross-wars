package com.vkir.components

import com.vkir.model.SideOfTheForce
import kotlinx.css.Color
import kotlinx.html.HTMLTag
import react.RBuilder
import react.dom.RDOMBuilder
import react.dom.tag

fun getUserColor(sideOfTheForce: SideOfTheForce, swordColor: Int): Color {
    val colorId = swordColor % 3
    return when(sideOfTheForce) {
        SideOfTheForce.Light ->
            when (colorId) {
                0 -> Color.limeGreen
                1 -> Color.dodgerBlue
                2 -> Color.whiteSmoke
                else -> Color.yellow
            }
        SideOfTheForce.Dark ->
            when (colorId) {
                0 -> Color.red
                1 -> Color.coral
                2 -> Color.darkViolet
                else -> Color.yellow
            }
    }
}

inline fun RBuilder.custom(tagName: String, block: RDOMBuilder<HTMLTag>.() -> Unit) = tag(block) {
    HTMLTag(tagName, it, mapOf(), null, true, false)
}




