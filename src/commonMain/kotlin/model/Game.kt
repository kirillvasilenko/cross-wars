package model

import kotlinx.serialization.Serializable

@Serializable
enum class GameState{
    CREATED, ACTIVE, COMPLETED, ARCHIVED
}


@Serializable
data class Field(val x: Int, val y: Int)

@Serializable
data class UserInGame(val id: Int, val symbol: Int)

@Serializable
data class GameDto(
        val id: Int,
        val lastMovedDate: Long,
        val lastMovedUser: UserInGame?,
        val users: MutableList<UserInGame>,
        val board: List<MutableList<UserInGame?>>)