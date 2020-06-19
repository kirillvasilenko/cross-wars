package viewModels.mainScreen

import api.Api
import viewModels.common.CommandVm
import viewModels.common.ViewModel
import viewModels.common.VmEvent

class Logout(source: ViewModel): VmEvent(source)

class HeaderVm(val userName: String): CommandVm(){

    var mouseOver: Boolean = false
        set(value){
            if(field == value) return
            field = value
            raiseStateChanged()
        }

    val inscription: String
        get() = if(mouseOver) "Leave" else userName

    override suspend fun executeImpl() {
        raiseEvent(Logout(this))
    }

}