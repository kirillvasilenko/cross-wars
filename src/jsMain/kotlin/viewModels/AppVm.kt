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

    override suspend fun initImpl(){
        try{
            user = Api.auth.auth()
            // todo remove
            log(user.toString())

            val games = Api.games.getActiveGames()
        }
        catch(e:Throwable){
            log("error on authentication: ${e.message}")
        }

        if(user.currentGameId != null){
            val game = Api.games.getGame(user.currentGameId!!)
            startPlaying(game)
        }
        else{
            openMainScreen()
        }
    }

    private suspend fun startPlaying(game: GameDto){
        val newVm = PlayGameVm(user, game.id).apply {
            onLeaveGame = ::leaveFromGame
        }
        changeCurrentVm(newVm)
    }

    private suspend fun leaveFromGame(){
        openMainScreen()
    }

    private suspend fun openMainScreen(){
        val newVm = MainScreenVm(user).apply{
            onJoinedGame = ::startPlaying
        }
        changeCurrentVm(newVm)
    }

    private suspend fun changeCurrentVm(newVm: ViewModel){
        currentVm.dispose()
        currentVm = newVm
        raiseChanged()
    }
}