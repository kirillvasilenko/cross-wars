package model

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

open class GamesStorageInMemory{

    private val idCounter = AtomicInteger()
    private val gamesById = ConcurrentHashMap<Int, Game>()

    fun getGame(id: Int): Game
            = gamesById[id] ?: throw GameNotFoundException(id)

    fun getGames() : Collection<Game> {
        return gamesById.values
    }

    fun makeGame(): Game {
        val id = idCounter.incrementAndGet()
        val game = Game(id)
        gamesById[id] = game
        return game
    }

}

object GamesStorage: GamesStorageInMemory()

class GameNotFoundException(gameId: Int): UserFaultException("No game with id=$gameId")