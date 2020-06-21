package viewModels.playGameScreen

import viewModels.common.ViewModel

class LegendVm(val users: MutableList<UserInGameVm>): ViewModel(){

    fun usersChanged(){
        raiseStateChanged()
    }
}

