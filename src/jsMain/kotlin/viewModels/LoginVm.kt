package viewModels

import api.Api
import model.SideOfTheForce
import model.SignUpData
import viewModels.common.CommandVm
import viewModels.common.UserLoggedIn

class LoginVm: CommandVm(){

    override val canExecuted: Boolean
        get() = super.canExecuted && name.isNotBlank()

    var name: String = ""
        set(value) {
            if(field == value) return
            field = value
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

    override suspend fun executeImpl() {
        val user = Api.auth.signUp(SignUpData(name, sideOfTheForce, color))
        SubscriptionHub.raiseEvent(UserLoggedIn(this, user))
    }

}