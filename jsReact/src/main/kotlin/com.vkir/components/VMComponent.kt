package com.vkir.components

import com.vkir.viewModels.common.ViewModel
import kotlinx.coroutines.launch
import com.vkir.mainScope
import react.PropsWithChildren
import react.RComponent
import react.State

external interface VmProps<T>: PropsWithChildren where T: ViewModel {
    var pVm: T
}

external interface VmState: State {
    var version: Long
}

abstract class VMComponent<T>(props: VmProps<T>): RComponent<VmProps<T>, VmState>(props) where T: ViewModel {

    protected val vm: T
        get() = props.pVm

    override fun componentDidMount() {
        subscribeOnVmAndInit()
    }

    override fun componentWillUnmount() {
        unsubscribeFromVm()
    }

    override fun componentDidUpdate(prevProps: VmProps<T>, prevState: VmState, snapshot: Any) {
        subscribeOnVmAndInit()
    }

    override fun VmState.init(props: VmProps<T>) {
        version = vm.version
    }

    private fun subscribeOnVmAndInit() {
        vm.onStateChanged = {
            setState(
                transformState = {
                    it.version = vm.version
                    it
                }
            )
        }
        mainScope.launch {
            vm.init()
        }
    }

    private fun unsubscribeFromVm(){
        vm.onStateChanged = {}
    }

}