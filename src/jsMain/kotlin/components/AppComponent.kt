package components

import components.mainScreen.mainScreen
import components.playGameScreen.playGameScreen
import kotlinx.css.fontSize
import react.RBuilder
import react.ReactElement
import styled.css
import styled.styledDiv
import viewModels.AppVm
import viewModels.LoginVm
import viewModels.mainScreen.LoadScreenVm
import viewModels.mainScreen.MainScreenVm
import viewModels.playGameScreen.PlayGameVm


fun RBuilder.app(handler: VmProps<AppVm>.() -> Unit): ReactElement {
    return child(App::class) {
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
                is MainScreenVm -> mainScreen{ pVm = currentVm }
                is PlayGameVm -> playGameScreen { pVm = currentVm }
            }
        }
    }
}



