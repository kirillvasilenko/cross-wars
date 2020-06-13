package components

import kotlinx.css.*
import react.RBuilder
import react.ReactElement
import react.dom.p
import styled.StyledComponents.css
import styled.css
import styled.styledDiv
import viewModels.LegendVm
import viewModels.MainScreenVm
import viewModels.PlayGameVm

fun RBuilder.playGameScreen(handler: VmProps<PlayGameVm>.() -> Unit): ReactElement {
    return child(PlayGameScreen::class) {
        this.attrs(handler)
    }
}

class PlayGameScreen(props: VmProps<PlayGameVm>): VMComponent<PlayGameVm>(props) {

    override fun RBuilder.render() {
        styledDiv {
            css{
                display = Display.grid
                gridTemplateColumns = GridTemplateColumns(20.vw, 60.vw, 20.vw)
                height = 100.vh
            }
            styledDiv{
                css{
                    backgroundColor = Color.red
                }
                +"left"
            }
            styledDiv{
                css{
                    backgroundColor = Color.blue
                }
                +"center"
            }
            styledDiv{
                css{
                    backgroundColor = Color.yellow
                }
                +"right"
            }
        }
    }
}

fun RBuilder.legend(handler: VmProps<LegendVm>.() -> Unit): ReactElement {
    return child(Legend::class) {
        this.attrs(handler)
    }
}

class Legend(props: VmProps<LegendVm>): VMComponent<LegendVm>(props) {

    override fun RBuilder.render() {
        styledDiv {
            css{

            }
            vm.users.forEach {
                p{

                }
            }
        }
    }
}