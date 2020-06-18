package viewModels.playGameScreen

import viewModels.common.ViewModel

class GameResultsVm: ViewModel(){

    var resultMessage: String? = null
        private set(value){
            field = value
            raiseStateChanged()
        }

    fun userWin(user: UserInGameVm){
        resultMessage = "${user.userName} won! Cry enemies!"
    }

    fun draw(){
        resultMessage = "It's draw. Forces were equal."
    }

}