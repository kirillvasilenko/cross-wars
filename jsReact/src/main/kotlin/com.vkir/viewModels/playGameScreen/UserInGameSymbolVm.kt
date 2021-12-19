package com.vkir.viewModels.playGameScreen

import com.vkir.model.SideOfTheForce
import com.vkir.viewModels.common.ViewModel

data class UserInGameSymbolVm(
        val userSymbol: Int,
        val sideOfTheForce: SideOfTheForce,
        val swordColor: Int,
        var glowable: Boolean = true,
        var aLittleHidden: Boolean = false
) : ViewModel()