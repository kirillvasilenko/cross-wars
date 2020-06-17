package viewModels.mainScreen

import model.GameDto
import model.UserDto
import viewModels.ViewModel

class MainScreenVm(val user: UserDto): ViewModel(){

    val gamesListVm = child(GamesListVm())
}