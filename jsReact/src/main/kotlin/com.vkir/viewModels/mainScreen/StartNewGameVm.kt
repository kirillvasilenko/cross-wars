package com.vkir.viewModels.mainScreen

import com.vkir.api.Api
import com.vkir.viewModels.common.CommandVm

class StartNewGameVm: CommandVm(){

    override suspend fun executeImpl() {
        Api.games.startNewGame()
    }

}