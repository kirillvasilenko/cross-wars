package com.vkir.components.playGameScreen

import com.vkir.components.VMComponent
import com.vkir.components.VmProps
import com.vkir.viewModels.playGameScreen.LeaveGameVm
import kotlinx.coroutines.launch
import kotlinx.css.pct
import kotlinx.css.width
import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import com.vkir.mainScope
import react.RBuilder
import react.dom.attrs
import styled.css
import styled.styledButton

fun RBuilder.leaveGameButton(handler: VmProps<LeaveGameVm>.() -> Unit) {
    child(LeaveGameButton::class) {
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