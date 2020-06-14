package viewModels

import Api
import model.GameDto
import model.SideOfTheForce
import model.UserDto
import viewModels.mainScreen.LoadScreenVm
import viewModels.mainScreen.MainScreenVm
import viewModels.playGameScreen.PlayGameVm
import kotlin.browser.window

class AppVm: ViewModel() {

    var user = UserDto(-1, "...", null, SideOfTheForce.Light, 0)

    var currentVm:ViewModel = LoadScreenVm()

    override suspend fun init(){
        user = Api.auth.auth()
        if(user.currentGameId != null){
            val game = Api.games.getGame(user.currentGameId!!)
            startPlaying(game)
        }
        else{
            openMainScreen()
        }
    }

    private suspend fun startPlaying(game: GameDto){
        currentVm = PlayGameVm(user, game).apply {
            onLeaveGame = ::leaveFromGame
        }
        currentVm.init()
        raiseChanged()
    }

    private fun leaveFromGame(){
        openMainScreen()
    }

    private fun openMainScreen(){
        currentVm = MainScreenVm(user).apply{
            onStartedNewGame = ::startPlaying
        }
        raiseChanged()
    }

}