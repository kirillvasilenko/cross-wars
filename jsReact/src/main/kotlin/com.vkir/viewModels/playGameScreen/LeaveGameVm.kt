package com.vkir.viewModels.playGameScreen

import com.vkir.api.Api
import com.vkir.viewModels.common.CommandVm

class LeaveGameVm: CommandVm(){
    override suspend fun executeImpl() {
        Api.games.leaveCurrentGame()
    }
}