package viewModels

import model.GameDto
import model.UserInGame

class UserInGameVm(userInGame: UserInGame):ViewModel(){

    private val userId = userInGame.id
    val userSymbol = userInGame.symbol
    var userName: String = "..."


    override suspend fun init() {
        Api.users.getUser(userId)
                .let { userName = it.name }

    }
}

class LeaveGameVm: CommandVm<Unit>(){
    override suspend fun executeImpl() {
        Api.games.leaveCurrentGame()
    }
}

class LegendVm(usersInGame: MutableList<UserInGame>): ViewModel(){

    val users: MutableList<UserInGameVm> = usersInGame.map{ UserInGameVm(it) }.toMutableList()

}

class PlayGameVm(game: GameDto): ViewModel(){}