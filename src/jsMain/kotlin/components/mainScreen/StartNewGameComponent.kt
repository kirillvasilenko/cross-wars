package components.mainScreen

import components.GlobalStyle
import components.VMComponent
import components.VmProps
import viewModels.mainScreen.StartNewGameVm
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onMouseOutFunction
import kotlinx.html.js.onMouseOverFunction
import mainScope
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement
import react.*
import styled.css
import styled.styledButton
import styled.styledDiv

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