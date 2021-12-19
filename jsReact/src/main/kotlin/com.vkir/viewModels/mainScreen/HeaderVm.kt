package com.vkir.viewModels.mainScreen

import com.vkir.viewModels.SubscriptionHub
import com.vkir.viewModels.common.CommandVm
import com.vkir.viewModels.common.UserLoggedOut

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
        SubscriptionHub.raiseEvent(UserLoggedOut(this))
    }

}