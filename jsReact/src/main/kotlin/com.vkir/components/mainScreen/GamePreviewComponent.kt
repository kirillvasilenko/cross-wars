package com.vkir.components.mainScreen

import com.vkir.components.GlobalStyle
import com.vkir.components.VMComponent
import com.vkir.components.VmProps
import com.vkir.viewModels.mainScreen.GamePreviewVm
import com.vkir.viewModels.mainScreen.LastMoveTimeVm
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import com.vkir.mainScope
import mu.KotlinLogging
import react.*
import react.dom.attrs
import react.dom.p
import styled.css
import styled.styledButton
import styled.styledDiv
import styled.styledP

private val log = KotlinLogging.logger {}

fun RBuilder.gamePreview(handler: VmProps<GamePreviewVm>.() -> Unit) {
    child(GamePreview::class) {
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

fun RBuilder.lastMoveTime(handler: VmProps<LastMoveTimeVm>.() -> Unit) {
    child(LastMoveTime::class) {
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