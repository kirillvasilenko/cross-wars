package com.vkir.viewModels.playGameScreen

import com.vkir.model.SideOfTheForce
import com.vkir.model.UserInGame
import com.vkir.viewModels.common.ViewModel

class UserInGameVm(user: UserInGame): ViewModel(){

    val userId = user.id

    val userSymbol = user.symbol

    var active = user.active
        set(value) {
            if(value == field) return
            field = value
            raiseStateChanged()
        }

    var userName: String = user.userName

    var sideOfTheForce: SideOfTheForce = user.sideOfTheForce

    var swordColor: Int = user.swordColor

    var userSymbolVm = UserInGameSymbolVm(userSymbol, sideOfTheForce, swordColor, false)

}