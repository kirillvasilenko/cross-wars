package components.mainScreen

import components.VMComponent
import components.VmProps
import viewModels.mainScreen.MainScreenVm
import react.*
import styled.styledDiv


fun RBuilder.mainScreen(handler: VmProps<MainScreenVm>.() -> Unit): ReactElement {
    return child(MainScreen::class) {
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