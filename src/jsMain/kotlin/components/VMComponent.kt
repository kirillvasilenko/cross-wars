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

external interface VmState<T>: RState where T: ViewModel {
    var vm: T
}

abstract class VMComponent<T>(props: VmProps<T>): RComponent<VmProps<T>, VmState<T>>(props) where T: ViewModel {

    protected val vm: T
        get() = state.vm

    override fun VmState<T>.init(props: VmProps<T>) {
        vm = props.pVm
        vm.onChanged = {
            setState{ vm = vm}
        }
        mainScope.launch {
            vm.init()
        }
    }
}