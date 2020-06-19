package components.mainScreen

import components.GlobalStyle
import components.VMComponent
import components.VmProps
import kotlinx.coroutines.launch
import viewModels.mainScreen.GamePreviewVm
import kotlinx.css.*
import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import mainScope
import react.*
import react.dom.p
import styled.css
import styled.styledButton
import styled.styledDiv
import styled.styledP
import log

import viewModels.mainScreen.LastMoveTimeVm

fun RBuilder.gamePreview(handler: VmProps<GamePreviewVm>.() -> Unit): ReactElement {
    return child(GamePreview::class) {
        this.attrs(handler)
    }
}


class GamePreview(props: VmProps<GamePreviewVm>): VMComponent<GamePreviewVm>(props) {

    override fun RBuilder.render() {
        styledButton {
            css{
                margin = GlobalStyle.margin
                padding = GlobalStyle.margin
                height = GlobalStyle.gamePreviewSize
                width = GlobalStyle.gamePreviewSize

                display = Display.grid
                gridTemplateRows = GridTemplateRows(1.fr, LinearDimension.minContent)
                textAlign = TextAlign.left

                if(!vm.visible){
                    display = Display.none
                }
            }

            attrs{
                classes = setOf("button")
                onClickFunction = {
                    mainScope.launch{
                        vm.execute()
                    }
                }
            }

            styledDiv{
                vm.activeUsers.forEach { user ->
                    styledP{
                        +user.userName
                    }
                }
            }

            styledDiv{
                lastMoveTime { pVm = vm.lastMoveTimeVm }
                styledP{
                    +"Filled: ${vm.boardFilled}%"
                }
            }

        }
    }

}

fun RBuilder.lastMoveTime(handler: VmProps<LastMoveTimeVm>.() -> Unit): ReactElement {
    return child(LastMoveTime::class) {
        this.attrs(handler)
    }
}


class LastMoveTime(props: VmProps<LastMoveTimeVm>): VMComponent<LastMoveTimeVm>(props) {

    override fun RBuilder.render() {
        p{
            +"Last move: ${vm.lastMoveWasTimeAgo}"
        }
    }
}