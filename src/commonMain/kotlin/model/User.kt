package model

import kotlinx.serialization.Serializable

@Serializable
enum class SideOfTheForce{
    Light, Dark
}

@Serializable
data class UserDto(
        val id: Int,
        val name: String,
        val currentGameId: Int?,
        val sideOfTheForce: SideOfTheForce,
        val swordColor: Int)