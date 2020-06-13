package components.playGameScreen

import components.GlobalStyle
import components.VMComponent
import components.VmProps
import kotlinx.css.*
import react.RBuilder
import react.ReactElement
import styled.css
import styled.styledDiv
import viewModels.playGameScreen.LegendVm
import viewModels.playGameScreen.UserInLegendVm
import kotlin.Float

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

fun RBuilder.userInLegend(handler: VmProps<UserInLegendVm>.() -> Unit): ReactElement {
    return child(UserInLegend::class) {
        this.attrs(handler)
    }
}

class UserInLegend(props: VmProps<UserInLegendVm>): VMComponent<UserInLegendVm>(props) {

    override fun RBuilder.render() {
        styledDiv {
            css{
                overflow = Overflow.hidden
            }
            if(!vm.initialized){
                styledDiv{
                    css{
                        float = kotlinx.css.Float.left
                    }
                    +"..."
                }
                return@styledDiv
            }

            styledDiv{
                css{
                    float = kotlinx.css.Float.left
                }
                +vm.userName
            }
            styledDiv {
                css{
                    float = kotlinx.css.Float.right
                    backgroundColor = Color.yellowGreen
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