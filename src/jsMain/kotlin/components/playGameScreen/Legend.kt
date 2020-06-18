package components.playGameScreen

import components.GlobalStyle
import components.VMComponent
import components.VmProps
import kotlinx.css.*
import kotlinx.css.properties.TextDecoration
import kotlinx.css.properties.TextDecorationLine
import react.RBuilder
import react.ReactElement
import styled.css
import styled.styledDiv
import viewModels.playGameScreen.LegendVm
import viewModels.playGameScreen.UserInGameVm

fun RBuilder.legend(handler: VmProps<LegendVm>.() -> Unit): ReactElement {
    return child(Legend::class) {
        this.attrs(handler)
    }
}

class Legend(props: VmProps<LegendVm>): VMComponent<LegendVm>(props) {

    override fun RBuilder.render() {
        styledDiv {
            vm.users.forEach {
                userInLegend{
                    pVm = it
                }
            }
        }
    }
}

fun RBuilder.userInLegend(handler: VmProps<UserInGameVm>.() -> Unit): ReactElement {
    return child(UserInLegend::class) {
        this.attrs(handler)
    }
}

class UserInLegend(props: VmProps<UserInGameVm>): VMComponent<UserInGameVm>(props) {

    override fun RBuilder.render() {
        styledDiv {
            css{
                overflow = Overflow.hidden
            }
            styledDiv{
                css{
                    float = kotlinx.css.Float.left
                    if(!vm.active) {
                        textDecoration = TextDecoration(setOf(TextDecorationLine.lineThrough))
                    }
                }
                +vm.userName
            }
            styledDiv {
                css{
                    float = kotlinx.css.Float.right
                    height = GlobalStyle.userSymbolSize
                    width = GlobalStyle.userSymbolSize
                }
                userInGameSymbol {
                    pVm = vm.userSymbolVm

                }
            }
        }
    }
}