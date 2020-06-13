package viewModels.mainScreen

import Api
import model.GameDto
import model.UserInGame
import viewModels.CommandVm
import kotlin.js.Date

class GamePreviewVm(game: GameDto): CommandVm<GameDto>() {

    val gameId: Int = game.id

    var users: MutableList<UserInGame> = game.users

    val usersCount = users.size

    private var lastMovedDate = Date(game.lastMovedDate)

    val lastMovedTime: String
        get() =
            if (lastMovedDate.getTime() > 0.0) lastMovedDate.toLocaleTimeString("en")
            else "--"

    override suspend fun executeImpl(): GameDto {
        return Api.games.joinGame(gameId)
    }

}