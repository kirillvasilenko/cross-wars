package com.vkir.viewModels.mainScreen

import com.vkir.model.UserDto
import com.vkir.viewModels.common.ViewModel

class MainScreenVm(currentUser: UserDto): ViewModel(){

    val gamesListVm = GamesListVm()

    val headerVm = HeaderVm(currentUser.name)

    override suspend fun disposeImpl() {
        gamesListVm.dispose()
        headerVm.dispose()
    }
}