package components.playGameScreen

import components.GlobalStyle
import components.VMComponent
import components.VmProps
import kotlinx.css.*
import react.RBuilder
import react.ReactElement
import styled.css
import styled.styledDiv
import viewModels.playGameScreen.PlayGameVm
import viewModels.playGameScreen.GameResultsVm

fun RBuilder.playGameScreen(handler: VmProps<PlayGameVm>.() -> Unit): ReactElement {
    return child(PlayGameScreen::class) {
        this.attrs(handler)
    }
}

class PlayGameScreen(props: VmProps<PlayGameVm>): VMComponent<PlayGameVm>(props) {

    override fun RBuilder.render() {
        if(!vm.initialized){
            return // todo loading screen
        }

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
                    margin = "auto ${GlobalStyle.margin}"
                }
                gameResults { pVm = vm.resultVm }
            }

            // center
            styledDiv{
                css{
                    height = 100.vh
                    width = 100.vh
                }
                gameBoard { pVm = vm.gameBoardVm }
            }

            // right
            styledDiv{
                css{
                    margin = "auto ${GlobalStyle.margin}"
                }
                legend{ pVm = vm.legendVm }
                leaveGameButton { pVm = vm.leaveGameVm }
            }
        }
    }
}

fun RBuilder.gameResults(handler: VmProps<GameResultsVm>.() -> Unit): ReactElement {
    return child(GameResults::class) {
        this.attrs(handler)
    }
}

class GameResults(props: VmProps<GameResultsVm>): VMComponent<GameResultsVm>(props) {

    override fun RBuilder.render() {
        styledDiv{
            if(vm.resultMessage != null){
                +vm.resultMessage!!
            }
        }
    }
}













