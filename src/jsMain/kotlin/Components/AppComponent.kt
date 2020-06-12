package Components

import ViewModels.AppState
import ViewModels.AppVm
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.css.fontSize
import kotlinx.css.px
import mainScope
import react.*
import styled.css
import styled.styledDiv

external interface AppComponentState: RState {
    var vm: AppVm
}

class App : RComponent<RProps, AppComponentState>() {
    override fun RBuilder.render() {
        styledDiv{
            css{
                fontSize = 22.px
            }

            val vm = state.vm
            if(vm.state == AppState.Loading){
                loadingScreen()
                return@styledDiv
            }
            if(vm.state == AppState.MainScreen){
                mainScreen{
                    user = state.vm.user
                }
                return@styledDiv
            }
        }
    }

    override fun AppComponentState.init() {
        vm = AppVm()
        vm.onStateChanged = {
            setState{ vm = vm }
        }

        mainScope.launch {
            delay(2000)
            vm.init()
        }
    }

}