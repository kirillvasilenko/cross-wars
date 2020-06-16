package components.mainScreen

import components.VMComponent
import components.VmProps
import components.VmState
import kotlinx.coroutines.launch
import viewModels.mainScreen.GamePreviewVm
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import mainScope
import react.*
import react.dom.p
import styled.css
import styled.styledDiv
import viewModels.log
import viewModels.mainScreen.LastMoveTimeVm

fun RBuilder.gamePreview(handler: VmProps<GamePreviewVm>.() -> Unit): ReactElement {
    return child(GamePreview::class) {
        this.attrs(handler)
    }
}


class GamePreview(props: VmProps<GamePreviewVm>): VMComponent<GamePreviewVm>(props) {

    init{
        log("game component: create")
    }

    override fun RBuilder.render() {
        styledDiv {
            css{
                borderStyle = BorderStyle.solid
                borderColor = Color.green
                height = LinearDimension("250px")
                width = LinearDimension("250px")
                if(!vm.visible){
                    display = Display.none
                }
            }

            attrs{
                onClickFunction = {
                    mainScope.launch{
                        vm.execute()
                    }
                }
            }
            log("gameComponent ${vm.gameId}: render")
            p{
                +"id: ${vm.gameId}"
            }
            p{
                +"state: ${vm.state}"
            }
            p{
                +"version: ${state.version}"
            }
            p{
                +"Players: ${vm.activeUsersCount}"
            }
            lastMoveTime { pVm = vm.lastMoveTimeVm }

        }
    }

    override fun componentDidMount() {
        log("componentDidMount: version=${vm.version}")
        super.componentDidMount()
    }

    override fun componentDidUpdate(prevProps: VmProps<GamePreviewVm>, prevState: VmState, snapshot: Any) {
        log("componentDidUpdate: version=${vm.version}")
        super.componentDidUpdate(prevProps, prevState, snapshot)
    }

}

fun RBuilder.lastMoveTime(handler: VmProps<LastMoveTimeVm>.() -> Unit): ReactElement {
    return child(LastMoveTime::class) {
        this.attrs(handler)
    }
}


class LastMoveTime(props: VmProps<LastMoveTimeVm>): VMComponent<LastMoveTimeVm>(props) {

    override fun RBuilder.render() {
        p{
            +"Last move: ${vm.lastMoveWasTimeAgo}"
        }
    }
}