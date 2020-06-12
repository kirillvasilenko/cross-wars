package Components

import ViewModels.GameVm
import kotlinx.css.*
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

external interface GameProps: RProps {
    var game: GameVm
}


fun RBuilder.gamePreview(handler: GameProps.() -> Unit): ReactElement {
    return child(Game::class) {
        this.attrs(handler)
    }
}

class Game: RComponent<GameProps, RState>() {
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


            val game = props.game
            p{
                +"Players: ${game.usersCount}"
            }
            p{
                +"Last move: ${game.lastMovedTime}"
            }

        }
    }
}