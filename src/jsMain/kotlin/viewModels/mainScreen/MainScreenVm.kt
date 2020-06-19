package viewModels.mainScreen

import model.UserDto
import viewModels.common.ViewModel

class MainScreenVm(val currentUser: UserDto): ViewModel(){

    val gamesListVm = child(GamesListVm(currentUser.id))

    val headerVm = child(HeaderVm(currentUser.name))
}