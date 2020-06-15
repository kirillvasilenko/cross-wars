package model

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

open class GamesStorageInMemory{

    private val idCounter = AtomicInteger()
    private val gamesById = ConcurrentHashMap<Int, Game>()

    fun getGame(id: Int): Game
            = gamesById[id] ?: throw GameNotFoundException(id)

    fun getActiveGames() : Collection<Game> {
        return gamesById.values
                .filter{ it.state == GameState.ACTIVE }
                .sortedByDescending { it.createdTime }
    }

    fun contains(id: Int) = gamesById.containsKey(id)

    fun makeGame(): Game {
        val id = idCounter.incrementAndGet()
        val game = Game(id)
        gamesById[id] = game
        return game
    }

}

object GamesStorage: GamesStorageInMemory()

class GameNotFoundException(gameId: Int): UserFaultException("No game with id=$gameId")