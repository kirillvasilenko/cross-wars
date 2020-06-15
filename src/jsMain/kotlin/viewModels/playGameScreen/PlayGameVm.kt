package viewModels.playGameScreen

import Api
import model.GameDto
import model.UserDto
import viewModels.ViewModel

class PlayGameVm(val currentUser: UserDto, game: GameDto): ViewModel(){

    var game: GameDto = game
        private set

    var onLeaveGame: suspend () -> Unit = {}

    val leaveGameVm = LeaveGameVm()

    lateinit var users: MutableList<UserInGameVm>
        private set

    lateinit var legendVm: LegendVm
        private set

    lateinit var gameBoardVm: GameBoardVm
        private set

    init{
        initialized = false
        leaveGameVm.onExecuted = {
            onLeaveGame()
        }
    }

    private suspend fun loadData(){
        users = game.users
                .map { userInGame ->
                    val userDto = Api.users.getUser(userInGame.id)
                    UserInGameVm(userDto, userInGame)
                }.toMutableList()
        legendVm = LegendVm(currentUser, users)
        gameBoardVm = GameBoardVm(currentUser, users, game.board)
    }

    override suspend fun init() {
        if(initialized) return
        loadData()
        initialized = true
        raiseChanged()
    }



}

