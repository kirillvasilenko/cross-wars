package components

import kotlinx.css.Color
import kotlinx.css.px
import kotlinx.html.HTMLTag
import model.SideOfTheForce
import react.RBuilder
import react.RProps
import react.RState
import react.ReactElement
import react.dom.RDOMBuilder
import react.dom.tag
import viewModels.ViewModel

object GlobalStyle{

    val mainColor = Color.green

    val fontSize = 22.px
    val userSymbolSize = 28.px
    val symbolStrokeWidth = 5

    val borderWidth = 2.px
}

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

inline fun RBuilder.custom(tagName: String, block: RDOMBuilder<HTMLTag>.() -> Unit): ReactElement = tag(block) {
    HTMLTag(tagName, it, mapOf(), null, true, false)
}




