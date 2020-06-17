package components

import kotlinx.css.Color
import kotlinx.css.margin
import kotlinx.css.px
import kotlinx.html.HTMLTag
import model.SideOfTheForce
import react.RBuilder
import react.ReactElement
import react.dom.RDOMBuilder
import react.dom.tag

object GlobalStyle{

    val mainColor = Color.green
    val backgroundColor = Color.black

    val fontSize = 22.px
    val headerFontSize = 30.px
    val logoFontSize = 80.px
    val loadingScreenFontSize = 100.px

    val margin = "4px"

    val gamePreviewSize = 250.px

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




