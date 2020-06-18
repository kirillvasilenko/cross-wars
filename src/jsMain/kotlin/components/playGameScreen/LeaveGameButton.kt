package components.playGameScreen

import components.VMComponent
import components.VmProps
import kotlinx.coroutines.launch
import kotlinx.css.LinearDimension
import kotlinx.css.pct
import kotlinx.css.width
import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import mainScope
import react.RBuilder
import react.ReactElement
import styled.css
import styled.styledButton
import viewModels.playGameScreen.LeaveGameVm

fun RBuilder.leaveGameButton(handler: VmProps<LeaveGameVm>.() -> Unit): ReactElement {
    return child(LeaveGameButton::class) {
        this.attrs(handler)
    }
}

class LeaveGameButton(props: VmProps<LeaveGameVm>): VMComponent<LeaveGameVm>(props) {

    override fun RBuilder.render() {
        styledButton {
            css{
                width = 100.pct
            }
            attrs{
                classes = setOf("button")
                if(!vm.canExecuted){
                    disabled = true
                }
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