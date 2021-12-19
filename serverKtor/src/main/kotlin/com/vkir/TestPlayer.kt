package com.vkir

import com.vkir.model.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KotlinLogging
import kotlin.random.Random

private val log = KotlinLogging.logger {}

suspend fun runTestPlayers() = coroutineScope {

    val usersCount = 12
    val maxDelaySec = 3

    val testUsers = (1..usersCount).map {
        UsersStorage.generateUser()
    }

    val testPlayers = testUsers.map {
        TestPlayer(it, testUsers, maxDelaySec)
    }

    testPlayers.forEach { player ->
        launch {
            player.play()
        }
    }
}

class TestPlayer(val user: User, val allTestUsers: List<User>, val maxDelaySec: Int) {

    suspend fun play() {
        while (true) {
            try {
                val game = chooseGame()
                playGame(game)
            } catch (e: Throwable) {
                log.debug(e.message)
            }
        }
    }

    private suspend fun randomDelay() {
        delay(Random.nextInt(1, maxDelaySec + 1).toLong() * 1000)
    }

    private suspend fun chooseGame(): Game {
        randomDelay()
        if (user.currentGame != null) return user.currentGame!!

        val activeGames = GamesStorage.getActiveGames()
            .map { it.snapshot() }
            // Join only to other test user's games.
            // Not irritate real users.
            .filter { activeGame ->
                activeGame.users.any { userInGame ->
                    allTestUsers.any { testUser ->
                        userInGame.id == testUser.id
                    }
                }
            }
            .toList()

        if (activeGames.isEmpty()) {
            return user.startNewGame()
        }

        var countUsers = 1
        var probabilityToJoin = 0.9
        var currentGames = activeGames.filter { it.users.size == countUsers }
        while (currentGames.any()) {
            if (Random.nextDouble() < probabilityToJoin) {
                val randomGame = currentGames[Random.nextInt(currentGames.size)]
                return user.joinGame(randomGame.id)
            }

            countUsers++
            probabilityToJoin /= 3
            currentGames = activeGames.filter { it.users.size == countUsers }
        }

        return user.startNewGame()
    }

    private suspend fun playGame(game: Game) {
        while (game.state == GameState.ACTIVE) {
            randomDelay()

            val snapshot = game.snapshot()

            val vacantFields = snapshot.board.mapIndexed { x, row ->
                row.mapIndexed { y, userInGame ->
                    if (userInGame == null) Field(x, y)
                    else null
                }
            }.flatten().filterNotNull().toList()

            if (vacantFields.isEmpty()) break

            if (snapshot.users.size < 2
                || snapshot.lastMovedUserId == user.id
            ) {
                continue
            }

            val randomField = vacantFields[Random.nextInt(vacantFields.size)]
            try {
                user.makeMove(randomField.x, randomField.y)
            } catch (e: Throwable) {
                // do nothing
            }
        }
        user.leaveCurrentGame()
    }
}