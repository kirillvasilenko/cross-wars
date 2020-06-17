package viewModels

import Api
import io.ktor.client.features.ClientRequestException
import io.ktor.http.HttpStatusCode
import model.GameDto
import model.SideOfTheForce
import model.UserDto
import viewModels.mainScreen.LoadScreenVm
import viewModels.mainScreen.MainScreenVm
import viewModels.playGameScreen.PlayGameVm

class AppVm: ViewModel() {

    var user = UserDto(-1, "...", null, SideOfTheForce.Light, 0)

    var currentVm:ViewModel = LoadScreenVm()

    override suspend fun initImpl(){
        try{
            user = Api.account.getCurrentUser()
        }
        catch(e:ClientRequestException){
            if(e.response.status == HttpStatusCode.Unauthorized){
                user = Api.auth.signUpAnonymous()
            }
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