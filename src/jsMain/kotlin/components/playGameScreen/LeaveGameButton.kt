package components.playGameScreen

import components.VMComponent
import components.VmProps
import kotlinx.coroutines.launch
import kotlinx.html.js.onClickFunction
import mainScope
import react.RBuilder
import react.ReactElement
import styled.styledDiv
import viewModels.playGameScreen.LeaveGameVm

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