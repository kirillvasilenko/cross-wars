package viewModels.playGameScreen

import model.SideOfTheForce
import model.UserDto
import model.UserInGame
import viewModels.common.ViewModel

class UserInGameVm(user: UserDto, userInGame: UserInGame): ViewModel(){

    val userId = user.id

    val userSymbol = userInGame.symbol

    var active = userInGame.active
        set(value) {
            if(value == field) return
            field = value
            raiseStateChanged()
        }

    var userName: String = user.name

    var sideOfTheForce: SideOfTheForce = user.sideOfTheForce

    var swordColor: Int = user.swordColor


    var userSymbolVm = UserInGameSymbolVm(userSymbol, sideOfTheForce, swordColor)

}