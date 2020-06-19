package viewModels

import api.Api
import io.ktor.client.features.ClientRequestException
import io.ktor.http.HttpStatusCode
import log
import model.SideOfTheForce
import model.UserDto
import viewModels.common.ErrorHappened
import viewModels.common.Unauthorized
import viewModels.common.ViewModel
import viewModels.common.VmEvent
import viewModels.mainScreen.*
import viewModels.playGameScreen.UserLeavedCurrentGame
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
            is Logout -> logout()
            is UserJoinedGame -> startPlaying(event.gameId)
            is UserStartedNewGame -> startPlaying(event.gameId)
            is UserLeavedCurrentGame -> openMainScreen()
            is ErrorHappened ->
                when(event){
                    is Unauthorized -> openLoginForm()
                    else -> log(event.cause.message)
                }
        }
    }

    //region changing layout

    private suspend fun userLogged(currentUser: UserDto){
        user = currentUser
        SubscriptionHub.startConnecting()
        if(user.currentGameId != null){
            startPlaying(user.currentGameId!!)
        }
        else{
            openMainScreen()
        }
    }

    private suspend fun openLoginForm(){
        changeCurrentVm(LoginVm())
    }

    private suspend fun startPlaying(gameId: Int){
        changeCurrentVm(PlayGameVm(user, gameId))
    }

    private suspend fun openMainScreen(){
        changeCurrentVm(MainScreenVm(user))
    }

    private suspend fun logout(){
        removeChild(currentVm)
        SubscriptionHub.stopConnecting()
        Api.auth.logout()
        openLoginForm()
    }

    //endregion changing layout


    private suspend fun changeCurrentVm(newVm: ViewModel){
        removeChild(currentVm)
        currentVm = newVm
        child(newVm)
        raiseStateChanged()
    }


}