package viewModels.mainScreen

import OpenWsArgs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mainScope
import model.*
import viewModels.ViewModel

class GamesListVm: ViewModel(){

    val startNewGameVm = StartNewGameVm()

    val games = mutableListOf<GamePreviewVm>()

    var onJoinedGame: suspend (GameDto) -> Unit = {}

    init{
        startNewGameVm.onExecuted = ::raiseJoinedGame
    }



    override suspend fun init() {
        Api.games.getGames()
                .map {
                    GamePreviewVm(it)
                }
                .forEach {
                    it.onExecuted = ::raiseJoinedGame
                    games.add(it)
                }
        onChanged()
        SubscriptionHub.subscribeCommonEvents({}, {})
    }

    private suspend fun raiseJoinedGame(game: GameDto){
        onJoinedGame(game)
    }
}

class WsConnection{

    var connected = false
        private set

    var textHandler: suspend (String) -> Unit = {}

    var onConnectionSet: () -> Unit = {}

    init{
        openInfiniteWsConnection()
    }

    private suspend fun handleText(text:String) = textHandler(text)

    private suspend fun wsConnectionSet(){
        connected = true
        onConnectionSet()
    }

    private fun openInfiniteWsConnection(){
        mainScope.launch{
            try {
                Api.subscriptions.openWsConnection(OpenWsArgs(::handleText,::wsConnectionSet))
            }
            catch(e:Throwable) {
                println(e.message)
            }
            finally{
                println("close ws connection")
                connected = false
                //delay(1000)
                //openInfiniteWsConnection()
            }
        }
    }

}

object SubscriptionHub{

    private val connection = WsConnection()

    private var commonEventsHandler: (GameEvent) -> Unit = {}

    private var certainGameEventsHandler: (GameEvent) -> Unit = {}


    init{
        connection.textHandler = ::handleText
    }

    private suspend fun handleText(text: String){
        val event = EventsSerializer.parse(text)
        println(event)
        if(event is CertainGameEvent){

        }
        if(event is CommonGameEvent){

        }
        if(event is SpecificUserOnlyEvent){

        }
    }

    suspend fun subscribeCommonEvents(
            eventHandler: (GameEvent) -> Unit, connectionResetHandler: () -> Unit){
        commonEventsHandler = eventHandler
        Api.subscriptions.subscribeOnCommonEvents()
    }

}