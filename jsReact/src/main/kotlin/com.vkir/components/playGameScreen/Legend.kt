package com.vkir.components.playGameScreen

import com.vkir.components.GlobalStyle
import com.vkir.components.VMComponent
import com.vkir.components.VmProps
import com.vkir.viewModels.playGameScreen.LegendVm
import com.vkir.viewModels.playGameScreen.UserInGameVm
import kotlinx.css.*
import kotlinx.css.properties.TextDecoration
import kotlinx.css.properties.TextDecorationLine
import react.RBuilder
import styled.css
import styled.styledDiv

fun RBuilder.legend(handler: VmProps<LegendVm>.() -> Unit) {
    child(Legend::class) {
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

fun RBuilder.userInLegend(handler: VmProps<UserInGameVm>.() -> Unit) {
    child(UserInLegend::class) {
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
                    float = Float.left
                    if(!vm.active) {
                        textDecoration = TextDecoration(setOf(TextDecorationLine.lineThrough))
                    }
                }
                +vm.userName
            }
            styledDiv {
                css{
                    float = Float.right
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