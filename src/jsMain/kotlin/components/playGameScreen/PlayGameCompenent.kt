package components.playGameScreen

import components.GlobalStyle
import components.VMComponent
import components.VmProps
import kotlinx.css.*
import react.RBuilder
import react.ReactElement
import styled.css
import styled.styledDiv
import viewModels.playGameScreen.BoardFieldVm
import viewModels.playGameScreen.GameBoardVm
import viewModels.playGameScreen.PlayGameVm

fun RBuilder.playGameScreen(handler: VmProps<PlayGameVm>.() -> Unit): ReactElement {
    return child(PlayGameScreen::class) {
        this.attrs(handler)
    }
}

class PlayGameScreen(props: VmProps<PlayGameVm>): VMComponent<PlayGameVm>(props) {

    override fun RBuilder.render() {
        styledDiv {
            css{
                height = 100.vh
                width = 100.vw

                display = Display.grid
                gridTemplateColumns = GridTemplateColumns(1.fr, 100.vh, 1.fr)
            }
            // left
            styledDiv{
                css{
                    backgroundColor = Color.red
                }
                +"left"
            }

            // center
            styledDiv{
                css{
                    backgroundColor = Color.blue
                    height = 100.vh;
                    width = 100.vh;
                }
                gameBoard { pVm = vm.gameBoardVm }
            }

            // right
            styledDiv{
                css{
                    backgroundColor = Color.yellow
                    margin = "auto 0px"
                }
                legend{ pVm = vm.legendVm }
                leaveGameButton { pVm = vm.leaveGameVm }
            }
        }
    }
}













