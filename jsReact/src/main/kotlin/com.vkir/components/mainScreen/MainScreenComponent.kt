package com.vkir.components.mainScreen

import com.vkir.components.VMComponent
import com.vkir.components.VmProps
import com.vkir.viewModels.mainScreen.MainScreenVm
import react.*
import styled.styledDiv


fun RBuilder.mainScreen(handler: VmProps<MainScreenVm>.() -> Unit) {
    child(MainScreen::class) {
        this.attrs(handler)
    }
}

class MainScreen(props: VmProps<MainScreenVm>): VMComponent<MainScreenVm>(props) {

    override fun RBuilder.render() {
        styledDiv {
            header{
                pVm = vm.headerVm
            }
            gamesList{
                pVm = vm.gamesListVm
            }
        }
    }

}