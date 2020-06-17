package viewModels

import Api
import model.SideOfTheForce
import model.SignUpData
import model.UserDto
import viewModels.common.CommandVm
import viewModels.common.ViewModel
import viewModels.common.VmEvent

class UserLogin(source: ViewModel, val user: UserDto): VmEvent(source)

class LoginVm: CommandVm(){

    override val canExecuted: Boolean
        get() = super.canExecuted && name.isNotBlank()

    var name: String = ""
        set(value) {
            if(field == value) return
            field = value
            log(value)
            raiseStateChanged()
        }

    var sideOfTheForce: SideOfTheForce = SideOfTheForce.Light
        set(value) {
            if(field == value) return
            field = value
            raiseStateChanged()
        }

    var color: Int = 0
        set(value) {
            if(field == value) return
            field = value
            raiseStateChanged()
        }

    override suspend fun executeImpl(): VmEvent {
        val user = Api.auth.signUp(SignUpData(name, sideOfTheForce, color))
        return UserLogin(this, user)
    }

}