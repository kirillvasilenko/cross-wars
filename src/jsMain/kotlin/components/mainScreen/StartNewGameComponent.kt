package components.mainScreen

import components.VMComponent
import components.VmProps
import viewModels.mainScreen.StartNewGameVm
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import mainScope
import react.*
import styled.css
import styled.styledDiv

fun RBuilder.startNewGameButton(handler: VmProps<StartNewGameVm>.() -> Unit): ReactElement {
    return child(StartNewGameComponent::class) {
        this.attrs(handler)
    }
}

class StartNewGameComponent(props: VmProps<StartNewGameVm>): VMComponent<StartNewGameVm>(props) {

    override fun RBuilder.render() {
        styledDiv {
            css{
                borderStyle = BorderStyle.solid
                borderColor = Color.green
                height = 250.px
                width = 250.px

                display = Display.flex
                justifyContent = JustifyContent.center
                alignItems = Align.center
                fontSize = 150.px
            }

            attrs{
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