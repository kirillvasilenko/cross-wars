package com.vkir.components.mainScreen

import com.vkir.components.GlobalStyle
import com.vkir.components.VMComponent
import com.vkir.components.VmProps
import com.vkir.viewModels.mainScreen.StartNewGameVm
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import com.vkir.mainScope
import react.RBuilder
import react.dom.attrs
import styled.css
import styled.styledButton

fun RBuilder.startNewGameButton(handler: VmProps<StartNewGameVm>.() -> Unit) {
    child(StartNewGameComponent::class) {
        this.attrs(handler)
    }
}

class StartNewGameComponent(props: VmProps<StartNewGameVm>): VMComponent<StartNewGameVm>(props) {

    override fun RBuilder.render() {
        styledButton {
            css{
                height = GlobalStyle.gamePreviewSize
                width = GlobalStyle.gamePreviewSize
                margin = GlobalStyle.margin

                fontSize = 150.px
            }

            attrs{
                classes = setOf("button")
                onClickFunction = {
                    mainScope.launch{
                        vm.execute()
                    }
                }
            }
            +"+"
        }
    }
}