package com.vkir.app

import com.vkir.model.GameDto
import com.vkir.model.GamesStorage
import com.vkir.model.UsersStorage

open class GamesServiceInMemory{

    suspend fun getGames(): Collection<GameDto>{
        return GamesStorage.getActiveGames().map { it.snapshot() }
    }

    suspend fun getGame(id: Int): GameDto
            = GamesStorage.getGame(id).snapshot()

    suspend fun startNewGame(userId: Int): GameDto {
        val user = UsersStorage.getUser(userId)
        return user.startNewGame().snapshot()
    }

    suspend fun joinGame(userId: Int, gameId: Int): GameDto {
        val user = UsersStorage.getUser(userId)
        return user.joinGame(gameId).snapshot()
    }

    suspend fun leaveCurrentGame(userId: Int){
        val user = UsersStorage.getUser(userId)
        user.leaveCurrentGame()
    }

    suspend fun makeMove(userId: Int, x: Int, y: Int) {
        val user = UsersStorage.getUser(userId)
        user.makeMove(x, y)
    }

}

object GamesService: GamesServiceInMemory()