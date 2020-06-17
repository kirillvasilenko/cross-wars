package viewModels.mainScreen

import model.UserDto
import viewModels.common.ViewModel

class MainScreenVm(val user: UserDto): ViewModel(){

    val gamesListVm = child(GamesListVm())
}