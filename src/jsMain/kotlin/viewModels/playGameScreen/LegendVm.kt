package viewModels.playGameScreen

import model.UserDto
import viewModels.ViewModel

class LegendVm(val currentUser: UserDto, val users: MutableList<UserInGameVm>): ViewModel(){

    fun usersChanged(){
        raiseChanged()
    }
}

