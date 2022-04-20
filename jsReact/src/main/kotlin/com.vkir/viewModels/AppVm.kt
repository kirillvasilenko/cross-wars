package com.vkir.viewModels

import com.vkir.api.Api
import com.vkir.model.*
import com.vkir.viewModels.common.*
import com.vkir.viewModels.mainScreen.LoadScreenVm
import com.vkir.viewModels.mainScreen.MainScreenVm
import com.vkir.viewModels.playGameScreen.PlayGameVm
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

class AppVm: ViewModel() {

    var user = UserDto(-1, "...", null, SideOfTheForce.Light, 0)

    var currentVm: ViewModel = LoadScreenVm()

    private val frontendEventsHandler = FrontendEventsHandler(::handleFrontendEvents)

    private val userEventsHandler = BackendEventsHandler(::handleUserEvents, ::subscribeOnUserEvents)

    init{
        SubscriptionHub.subscribeOnFrontendEvents(frontendEventsHandler)
    }

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

    //region changing layout

    private suspend fun userLogged(currentUser: UserDto){
        user = currentUser
        SubscriptionHub.startConnecting()
        subscribeOnUserEvents()

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
        currentVm.dispose()
        SubscriptionHub.stopConnecting()
        try {
            Api.auth.logout()
        }
        catch(e: Throwable){
            log.error(e) { "error on logout: ${e.message}" }
        }
        openLoginForm()
    }

    //endregion changing layout

    private suspend fun handleFrontendEvents(event: FrontendEvent){
        when(event){
            is UserLoggedIn -> userLogged(event.user)
            is UserLoggedOut -> logout()
            is ErrorHappened ->
                when(event){
                    is Unauthorized -> handleUnauthorized()
                    else -> log.error { event.cause.message }
                }
        }
    }

    private suspend fun handleUnauthorized(){
        currentVm.dispose()
        SubscriptionHub.stopConnecting()
        openLoginForm()
    }

    private suspend fun handleUserEvents(event: UserEvent) {
        when(event){
            is UserJoinedGame -> startPlaying(event.gameId)
            is UserLeavedGame -> openMainScreen()
        }
    }

    private suspend fun subscribeOnUserEvents(){
        SubscriptionHub.subscribeOnUserEvents(userEventsHandler)
    }

    private suspend fun changeCurrentVm(newVm: ViewModel){
        currentVm.dispose()
        currentVm = newVm
        raiseStateChanged()
    }


}