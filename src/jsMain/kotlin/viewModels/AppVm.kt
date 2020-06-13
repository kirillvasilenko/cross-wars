package viewModels

import Api
import model.GameDto
import model.UserDto

class AppVm: ViewModel() {

    var user = UserDto(-1, "...", null)

    var currentVm:ViewModel = LoadScreenVm()

    override suspend fun init(){
        user = Api.auth.auth()
        openMainScreen()
    }

    private fun startPlaying(game: GameDto){
        currentVm = PlayGameVm(game)
        raiseChanged()
    }

    private fun openMainScreen(){
        currentVm = MainScreenVm(user).apply{
            onStartedNewGame = ::startPlaying
        }
        raiseChanged()
    }

}