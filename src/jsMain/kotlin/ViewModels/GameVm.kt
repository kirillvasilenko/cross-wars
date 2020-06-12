package ViewModels

import model.UserDto
import kotlin.js.Date

class GameVm {

    var users = listOf<UserDto>()

    private var lastMovedDate = Date()

    val lastMovedTime: String
        get() =
            if (lastMovedDate.getTime() > 0.0) lastMovedDate.toLocaleTimeString("en")
            else "--"

    val usersCount = users.size

}