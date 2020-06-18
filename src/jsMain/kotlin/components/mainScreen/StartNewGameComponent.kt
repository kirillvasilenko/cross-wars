package components.mainScreen

import components.GlobalStyle
import components.VMComponent
import components.VmProps
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import mainScope
import react.RBuilder
import react.ReactElement
import styled.css
import styled.styledButton
import viewModels.mainScreen.StartNewGameVm

fun RBuilder.startNewGameButton(handler: VmProps<StartNewGameVm>.() -> Unit): ReactElement {
    return child(StartNewGameComponent::class) {
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