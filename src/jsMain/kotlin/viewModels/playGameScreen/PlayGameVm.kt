package viewModels.playGameScreen

import kotlinx.coroutines.delay
import model.GameDto
import model.SideOfTheForce
import model.UserInGame
import viewModels.CommandVm
import viewModels.ViewModel

class UserInGameSymbolVm(
        val userSymbol: Int,
        val sideOfTheForce: SideOfTheForce,
        val swordColor: Int,
        val glowable: Boolean = true) : ViewModel(){

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

class LeaveGameVm: CommandVm<Unit>(){
    override suspend fun executeImpl() {
        Api.games.leaveCurrentGame()
    }
}

class LegendVm(usersInGame: MutableList<UserInGame>): ViewModel(){

    val users: MutableList<UserInLegendVm> = usersInGame.map{ UserInLegendVm(it) }.toMutableList()

}

class PlayGameVm(game: GameDto): ViewModel(){

    val legendVm = LegendVm(game.users)

}

