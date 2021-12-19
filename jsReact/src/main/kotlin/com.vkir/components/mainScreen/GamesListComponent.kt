package com.vkir.components.mainScreen

import com.vkir.components.GlobalStyle
import com.vkir.components.VMComponent
import com.vkir.components.VmProps
import com.vkir.viewModels.mainScreen.GamesListVm
import kotlinx.css.*
import react.RBuilder
import styled.css
import styled.styledDiv

fun RBuilder.gamesList(handler: VmProps<GamesListVm>.() -> Unit) {
    child(GamesList::class) {
        this.attrs(handler)
    }
}

class GamesList(props: VmProps<GamesListVm>): VMComponent<GamesListVm>(props) {

    override fun RBuilder.render() {
        styledDiv {
            css {
                display = Display.flex
                flexWrap = FlexWrap.wrap
            }

            startNewGameButton {
                css{
                    margin = GlobalStyle.margin
                }
                pVm = vm.startNewGameVm
            }

            vm.games.forEach {
                gamePreview{
                    css{
                        margin = GlobalStyle.margin
                    }
                    pVm = it
                }
            }
        }
    }
}