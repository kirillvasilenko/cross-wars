package viewModels

import Api
import io.ktor.client.features.ClientRequestException
import io.ktor.http.HttpStatusCode
import model.GameDto
import model.GameStarted
import model.SideOfTheForce
import model.UserDto
import viewModels.mainScreen.JoinedGame
import viewModels.mainScreen.LoadScreenVm
import viewModels.mainScreen.MainScreenVm
import viewModels.mainScreen.NewGameStarted
import viewModels.playGameScreen.LeavedGame
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
                login()
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

    override suspend fun handleChildEvent(event: VmEvent) {
        when(event){
            is JoinedGame -> startPlaying(event.game)
            is NewGameStarted -> startPlaying(event.game)
            is LeavedGame -> openMainScreen()
            is ErrorHappened ->
                when(event){
                    is Unauthorized -> login()
                    else -> log(event.cause.message)
                }
            else -> null // ignore
        }
    }

    //region changing layout

    private suspend fun startPlaying(game: GameDto){
        val newVm = PlayGameVm(user, game.id)
        changeCurrentVm(newVm)
    }

    private suspend fun openMainScreen(){
        val newVm = MainScreenVm(user)
        changeCurrentVm(newVm)
    }

    //endregion changing layout



    private suspend fun login(){
        user = Api.auth.signUpAnonymous()
    }

    private suspend fun changeCurrentVm(newVm: ViewModel){
        removeChild(currentVm)
        currentVm = newVm
        child(newVm)
        raiseStateChanged()
    }
}