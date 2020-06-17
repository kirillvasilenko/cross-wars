package viewModels

import Api
import io.ktor.client.features.ClientRequestException
import io.ktor.http.HttpStatusCode
import model.GameDto
import model.SideOfTheForce
import model.UserDto
import viewModels.common.ErrorHappened
import viewModels.common.Unauthorized
import viewModels.common.ViewModel
import viewModels.common.VmEvent
import viewModels.mainScreen.JoinedGame
import viewModels.mainScreen.LoadScreenVm
import viewModels.mainScreen.MainScreenVm
import viewModels.mainScreen.NewGameStarted
import viewModels.playGameScreen.LeavedGame
import viewModels.playGameScreen.PlayGameVm

class AppVm: ViewModel() {

    var user = UserDto(-1, "...", null, SideOfTheForce.Light, 0)

    var currentVm: ViewModel = child(LoadScreenVm())

    override suspend fun initImpl(){
        try{
            val currentUser = Api.account.getCurrentUser()
            userLogged(currentUser)
        }
        catch(e:ClientRequestException){
            if(e.response.status == HttpStatusCode.Unauthorized){
                openLoginForm()
                return
            }
        }
    }

    override suspend fun handleChildEvent(event: VmEvent) {
        when(event){
            is UserLogin -> userLogged(event.user)
            is JoinedGame -> startPlaying(event.game)
            is NewGameStarted -> startPlaying(event.game)
            is LeavedGame -> openMainScreen()
            is ErrorHappened ->
                when(event){
                    is Unauthorized -> openLoginForm()
                    else -> log(event.cause.message)
                }
            else -> null // ignore
        }
    }

    //region changing layout

    private suspend fun userLogged(currentUser: UserDto){
        user = currentUser
        if(user.currentGameId != null){
            val game = Api.games.getGame(user.currentGameId!!)
            startPlaying(game)
        }
        else{
            openMainScreen()
        }
    }

    private suspend fun openLoginForm(){
        changeCurrentVm(LoginVm())
    }

    private suspend fun startPlaying(game: GameDto){
        changeCurrentVm(PlayGameVm(user, game.id))
    }

    private suspend fun openMainScreen(){
        changeCurrentVm(MainScreenVm(user))
    }

    //endregion changing layout





    private suspend fun changeCurrentVm(newVm: ViewModel){
        removeChild(currentVm)
        currentVm = newVm
        child(newVm)
        raiseStateChanged()
    }
}