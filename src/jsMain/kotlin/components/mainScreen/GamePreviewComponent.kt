package components.mainScreen

import components.VMComponent
import components.VmProps
import kotlinx.coroutines.launch
import viewModels.mainScreen.GamePreviewVm
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import mainScope
import react.*
import react.dom.p
import styled.css
import styled.styledDiv

fun RBuilder.gamePreview(handler: VmProps<GamePreviewVm>.() -> Unit): ReactElement {
    return child(GamePreview::class) {
        this.attrs(handler)
    }
}


class GamePreview(props: VmProps<GamePreviewVm>): VMComponent<GamePreviewVm>(props) {

    override fun RBuilder.render() {
        styledDiv {
            css{
                borderStyle = BorderStyle.solid
                borderColor = Color.green
                height = LinearDimension("250px")
                width = LinearDimension("250px")
            }

            attrs{
                onClickFunction = {
                    mainScope.launch{
                        vm.execute()
                    }
                }
            }

            p{
                +"Players: ${vm.activeUsersCount}"
            }
            p{
                +"Last move: ${vm.lastMovedTime}"
            }

        }
    }
}