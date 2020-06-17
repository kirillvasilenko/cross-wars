package components

import components.mainScreen.HeaderProps
import kotlinx.css.*
import react.*
import styled.css
import styled.styledDiv

fun RBuilder.loadingScreen(): ReactElement {
    return child(LoadingScreenComponent::class) {}
}

class LoadingScreenComponent: RComponent<HeaderProps, RState>() {
    override fun RBuilder.render() {

        styledDiv {
            css {
                fontSize = 100.px
                height = LinearDimension.fillAvailable
                width = LinearDimension.fillAvailable

                borderColor = Color.green

                display = Display.flex
                justifyContent = JustifyContent.center
                alignItems = Align.center
            }

            +"Cross Wars"

        }
    }
}