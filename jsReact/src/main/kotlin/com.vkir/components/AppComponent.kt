package com.vkir.components

import com.vkir.components.mainScreen.mainScreen
import com.vkir.components.playGameScreen.playGameScreen
import com.vkir.viewModels.AppVm
import com.vkir.viewModels.LoginVm
import com.vkir.viewModels.mainScreen.LoadScreenVm
import com.vkir.viewModels.mainScreen.MainScreenVm
import com.vkir.viewModels.playGameScreen.PlayGameVm
import kotlinx.css.fontSize
import react.RBuilder
import styled.css
import styled.styledDiv


fun RBuilder.app(handler: VmProps<AppVm>.() -> Unit) {
    child(App::class) {
        this.attrs(handler)
    }
}

class App(props: VmProps<AppVm>) : VMComponent<AppVm>(props) {

    override fun RBuilder.render() {
        styledDiv{
            css{
                fontSize = GlobalStyle.fontSize
            }
            when(val currentVm = vm.currentVm){
                is LoadScreenVm -> loadingScreen()
                is LoginVm -> loginForm { pVm = currentVm }
                is MainScreenVm -> mainScreen { pVm = currentVm }
                is PlayGameVm -> playGameScreen { pVm = currentVm }
            }
        }
    }
}



