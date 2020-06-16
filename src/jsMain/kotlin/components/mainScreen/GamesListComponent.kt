package components.mainScreen

import components.VMComponent
import components.VmProps
import kotlinx.css.Display
import kotlinx.css.FlexWrap
import kotlinx.css.display
import kotlinx.css.flexWrap
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
                pVm = vm.startNewGameVm
            }

            log("gamesListComponent: render ${vm.games.size} games")
            vm.games.forEach {
                gamePreview{
                    pVm = it
                }
            }
        }
    }
}