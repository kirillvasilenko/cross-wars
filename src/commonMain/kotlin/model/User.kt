package model

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(val id: Int, val name: String, val currentGameId: Int?)