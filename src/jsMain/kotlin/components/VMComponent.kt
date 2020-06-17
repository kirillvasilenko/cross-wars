package components

import kotlinx.coroutines.launch
import mainScope
import react.RComponent
import react.RProps
import react.RState
import react.setState
import viewModels.ViewModel

external interface VmProps<T>: RProps where T: ViewModel {
    var pVm: T
}

external interface VmState: RState {
    var version: Long
}

abstract class VMComponent<T>(props: VmProps<T>): RComponent<VmProps<T>, VmState>(props) where T: ViewModel {

    protected val vm: T
        get() = props.pVm

    override fun componentDidMount() {
        subscribeOnVmAndInit()
    }

    override fun componentDidUpdate(prevProps: VmProps<T>, prevState: VmState, snapshot: Any) {
        unsubscribeFromVm(prevProps.pVm)
        subscribeOnVmAndInit()
    }

    override fun VmState.init(props: VmProps<T>) {
        version = vm.version
    }

    private fun subscribeOnVmAndInit(){
        vm.onStateChanged = {
            setState{
                version = vm.version
            }
        }
        mainScope.launch {
            vm.init()
        }
    }

    private fun unsubscribeFromVm(prevVm: T){
        prevVm.onStateChanged = {}
    }

}