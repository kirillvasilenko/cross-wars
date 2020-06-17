package viewModels.playGameScreen

import model.UserDto
import viewModels.common.ViewModel

class LegendVm(val currentUser: UserDto, val users: MutableList<UserInGameVm>): ViewModel(){

    fun usersChanged(){
        raiseStateChanged()
    }
}

