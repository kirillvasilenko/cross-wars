package com.vkir.viewModels

import com.vkir.api.Api
import com.vkir.model.SideOfTheForce
import com.vkir.model.SignUpData
import com.vkir.viewModels.common.CommandVm
import com.vkir.viewModels.common.UserLoggedIn

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