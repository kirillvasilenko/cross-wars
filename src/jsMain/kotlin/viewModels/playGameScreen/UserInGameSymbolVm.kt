package viewModels.playGameScreen

import model.SideOfTheForce
import viewModels.common.ViewModel

data class UserInGameSymbolVm(
        val userSymbol: Int,
        val sideOfTheForce: SideOfTheForce,
        val swordColor: Int,
        var glowable: Boolean = true,
        var aLittleHidden: Boolean = false): ViewModel()