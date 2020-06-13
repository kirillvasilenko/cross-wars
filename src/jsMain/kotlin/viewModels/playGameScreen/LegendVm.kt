package viewModels.playGameScreen

import model.SideOfTheForce
import model.UserInGame
import viewModels.ViewModel

class LegendVm(usersInGame: MutableList<UserInGame>): ViewModel(){

    val users: MutableList<UserInLegendVm> = usersInGame.map{ UserInLegendVm(it) }.toMutableList()

}

class UserInLegendVm(userInGame: UserInGame): ViewModel(){

    private val userId = userInGame.id
    val userSymbol = userInGame.symbol
    var userName: String = "..."
    var sideOfTheForce: SideOfTheForce = SideOfTheForce.Light
    var swordColor: Int = 0

    var userSymbolVm = UserInGameSymbolVm(userSymbol, sideOfTheForce, swordColor)

    init{
        initialized = false
    }

    override suspend fun init() {
        Api.users.getUser(userId)
                .let {
                    userName = it.name
                    sideOfTheForce = it.sideOfTheForce
                    swordColor = it.swordColor
                }
        userSymbolVm = UserInGameSymbolVm(userSymbol, sideOfTheForce, swordColor)
        initialized = true
        raiseChanged()
    }
}