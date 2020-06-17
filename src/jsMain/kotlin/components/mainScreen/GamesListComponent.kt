package components.mainScreen

import components.GlobalStyle
import components.VMComponent
import components.VmProps
import kotlinx.css.*
import react.RBuilder
import react.ReactElement
import styled.css
import styled.styledDiv
import viewModels.log
import viewModels.mainScreen.GamesListVm

fun RBuilder.gamesList(handler: VmProps<GamesListVm>.() -> Unit): ReactElement {
    return child(GamesList::class) {
        this.attrs(handler)
    }
}

class GamesList(props: VmProps<GamesListVm>): VMComponent<GamesListVm>(props) {

    override fun RBuilder.render() {
        styledDiv {
            css {
                display = Display.flex
                flexWrap = FlexWrap.wrap
            }

            startNewGameButton {
                css{
                    margin = GlobalStyle.margin
                }
                pVm = vm.startNewGameVm
            }

            vm.games.forEach {
                gamePreview{
                    css{
                        margin = GlobalStyle.margin
                    }
                    pVm = it
                }
            }
        }
    }
}