package Components

import ViewModels.GameVm
import kotlinx.css.*
import kotlinx.css.properties.LineHeight
import kotlinx.html.js.onClickFunction
import kotlinx.html.onClick
import model.GameDto
import react.*
import react.dom.p
import styled.css
import styled.styledDiv
import styled.styledP
import kotlin.browser.window
import kotlin.js.Date

external interface StartNewGameProps: RProps {
    var onStartNewGame: () -> Unit
}


fun RBuilder.startNewGame(handler: StartNewGameProps.() -> Unit): ReactElement {
    return child(StartNewGameComponent::class) {
        this.attrs(handler)
    }
}

class StartNewGameComponent: RComponent<StartNewGameProps, RState>() {
    override fun RBuilder.render() {
        styledDiv {
            css{
                borderStyle = BorderStyle.solid
                borderColor = Color.green
                height = 250.px
                width = 250.px

                display = Display.flex
                justifyContent = JustifyContent.center
                alignItems = Align.center
                fontSize = 150.px
            }

            attrs{
                onClickFunction = {
                    window.alert("Start new Game!")
                    props.onStartNewGame()
                }
            }
            +"+"

        }
    }
}