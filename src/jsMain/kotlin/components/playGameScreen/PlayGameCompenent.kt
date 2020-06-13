package components.playGameScreen

import components.VMComponent
import components.VmProps
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import mainScope
import react.RBuilder
import react.ReactElement
import styled.css
import styled.styledDiv
import viewModels.playGameScreen.LeaveGameVm
import viewModels.playGameScreen.LegendVm
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
            styledDiv{
                css{
                    backgroundColor = Color.red
                }
                +"left"
            }
            styledDiv{
                css{
                    backgroundColor = Color.blue
                }
                +"center"
            }
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

fun RBuilder.leaveGameButton(handler: VmProps<LeaveGameVm>.() -> Unit): ReactElement {
    return child(LeaveGameButton::class) {
        this.attrs(handler)
    }
}

class LeaveGameButton(props: VmProps<LeaveGameVm>): VMComponent<LeaveGameVm>(props) {

    override fun RBuilder.render() {
        styledDiv {
            attrs{
                onClickFunction = {
                    mainScope.launch{
                        vm.execute()
                    }
                }
            }
            +"Leave"
        }
    }
}












