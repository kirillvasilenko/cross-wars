package model

import kotlinx.serialization.Serializable

@Serializable
enum class GameState{
    CREATED, ACTIVE, COMPLETED, ARCHIVED
}

@Serializable
data class Field(val x: Int, val y: Int)

@Serializable
data class UserInGame(
        val id: Int,
        val symbol: Int,
        var active:Boolean,
        val userName: String,
        val sideOfTheForce: SideOfTheForce,
        val swordColor: Int)

@Serializable
data class GameDto(
        val id: Int,
        var state: GameState,
        val lastMovedDate: Long,
        val lastMovedUserId: Int,
        val users: MutableList<UserInGame>,
        val board: List<MutableList<Int?>>)