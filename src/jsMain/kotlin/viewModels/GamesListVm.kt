package viewModels

class GamesListVm: ViewModel(){

    val startNewGameVm = StartNewGameVm()

    val games = mutableListOf<GamePreviewVm>()


    override suspend fun init() {
        Api.games.getGames().forEach {
            games.add(GamePreviewVm(it))
        }
        onChanged()
    }
}