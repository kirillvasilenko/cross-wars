package com.vkir.viewModels.playGameScreen

import com.vkir.viewModels.common.ViewModel

class LegendVm(val users: MutableList<UserInGameVm>): ViewModel(){

    fun usersChanged(){
        raiseStateChanged()
    }
}

