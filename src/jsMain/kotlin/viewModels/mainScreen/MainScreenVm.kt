package viewModels.mainScreen

import model.UserDto
import viewModels.common.ViewModel

class MainScreenVm(currentUser: UserDto): ViewModel(){

    val gamesListVm = GamesListVm()

    val headerVm = HeaderVm(currentUser.name)

    override suspend fun disposeImpl() {
        gamesListVm.dispose()
        headerVm.dispose()
    }
}