package viewModels.playGameScreen

import model.SideOfTheForce
import model.UserInGame
import viewModels.common.ViewModel

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