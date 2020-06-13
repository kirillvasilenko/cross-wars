package components

import viewModels.GamePreviewVm
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.p
import styled.css
import styled.styledDiv
import kotlin.browser.window

external interface GamePreviewProps: RProps {
    var gamePreviewVm: GamePreviewVm
}

external interface GamePreviewState: RState {
    var gamePreviewVm: GamePreviewVm
}

fun RBuilder.gamePreview(handler: GamePreviewProps.() -> Unit): ReactElement {
    return child(GamePreview::class) {
        this.attrs(handler)
    }
}





class GamePreview(props: GamePreviewProps): RComponent<GamePreviewProps, GamePreviewState>(props) {



    override fun RBuilder.render() {
        styledDiv {
            css{
                borderStyle = BorderStyle.solid
                borderColor = Color.green
                height = LinearDimension("250px")
                width = LinearDimension("250px")
            }

            attrs{
                onClickFunction = {
                    window.alert("Clicked!!!")

                }
            }


            val game = props.gamePreviewVm
            p{
                +"Players: ${game.usersCount}"
            }
            p{
                +"Last move: ${game.lastMovedTime}"
            }

        }
    }

    override fun GamePreviewState.init(props: GamePreviewProps) {
        gamePreviewVm = props.gamePreviewVm
    }
}