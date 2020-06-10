package model


open class GamesServiceInMemory{

    suspend fun getGames(): Collection<GameDto>{
        return GamesStorage.getGames().map{ it.toDto() }
    }

    suspend fun getGame(id: Int): GameDto
            = GamesStorage.getGame(id).toDto()

    suspend fun startNewGame(userId: Int): GameDto{
        val user = UsersStorage.getUser(userId)
        return user.startNewGame().toDto()
    }

    suspend fun joinGame(userId: Int, gameId: Int): GameDto{
        val user = UsersStorage.getUser(userId)
        return user.joinGame(gameId).toDto()
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

object GamesService:GamesServiceInMemory()