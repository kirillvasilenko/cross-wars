package components

import kotlinx.css.fontSize
import kotlinx.css.px
import react.RBuilder
import react.ReactElement
import styled.css
import styled.styledDiv
import viewModels.AppVm
import viewModels.LoadScreenVm
import viewModels.MainScreenVm
import viewModels.PlayGameVm

fun RBuilder.app(handler: VmProps<AppVm>.() -> Unit): ReactElement {
    return child(App::class) {
        this.attrs(handler)
    }
}

class App(props: VmProps<AppVm>) : VMComponent<AppVm>(props) {

    override fun RBuilder.render() {
        styledDiv{
            css{
                fontSize = 22.px
            }
            when(val currentVm = vm.currentVm){
                is LoadScreenVm -> loadingScreen()
                is MainScreenVm -> mainScreen{pVm = currentVm }
                is PlayGameVm -> playGameScreen { pVm = currentVm }
            }
        }
    }
}