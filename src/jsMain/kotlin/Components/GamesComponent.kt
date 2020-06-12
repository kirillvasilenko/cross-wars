package Components

import ViewModels.GameVm
import kotlinx.css.*
import react.*
import styled.css
import styled.styledDiv

external interface GamesProps: RProps {
    var games: List<GameVm>
    var onStartNewGame: () -> Unit
}


fun RBuilder.games(handler: GamesProps.() -> Unit): ReactElement {
    return child(Games::class) {
        this.attrs(handler)
    }
}

class Games: RComponent<GamesProps, RState>() {
    override fun RBuilder.render() {
        styledDiv {
            css {
                display = Display.flex
                flexWrap = FlexWrap.wrap
            }

            startNewGame {
                onStartNewGame = onStartNewGame
            }

            props.games.forEach {
                gamePreview{
                    game = it
                }
            }

        }
    }
}