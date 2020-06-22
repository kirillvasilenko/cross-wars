package viewModels

import api.Api
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import log
import mainScope
import model.WsCommand
import model.WsCommandsSerializer
import kotlin.math.max

class WsConnection{

    private val initialDelayToConnect = 1000

    private val maxDelayToConnect = 10_000

    private var delayToConnect = 1000

    var connecting = false
        private set

    var connected = false
        private set

    var textHandler: suspend (String) -> Unit = {}

    var onConnectionOpened: suspend () -> Unit = {}

    private var currentSession: WebSocketSession? = null

    fun startWsConnecting(){
        connecting = true
        mainScope.launch{
            while(connecting){
                startWsConnectingImpl()
            }
        }
    }

    suspend fun stopWsConnecting(){
        connecting = false
        currentSession?.close()
    }

    suspend fun sendCommand(command: WsCommand){
        try {
            val commandAsText = WsCommandsSerializer.stringify(command)
            currentSession?.outgoing?.send(Frame.Text(commandAsText))
        }
        catch(e: Throwable){
            log("error on sending command: $e.message")
        }
    }

    private suspend fun startWsConnectingImpl() {
        try {
            Api.ws.openWsConnection {
                log("ws connection is opened")
                currentSession = this
                onWsConnectionOpened()
                receiving(incoming)
                this.close()
            }
            delayToConnect = initialDelayToConnect
        } catch (e: Throwable) {
            log("error during connecting to ws api.getEndpoint: ${e.message}")
            delayToConnect = max(delayToConnect * 2, maxDelayToConnect)
        } finally {
            log("ws connection is closed")
            connected = false
            delay(delayToConnect.toLong())
        }
    }

    private suspend fun receiving(incoming: ReceiveChannel<Frame>){
        try {
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> handleText(frame.readText())
                    else -> log("ws received ${frame.frameType}")
                }
            }
        }
        catch(e:Throwable){
            log("some error in ws connection: ${e.message}")
        }
    }

    private suspend fun handleText(text:String){
        try{
            textHandler(text)
        }
        catch(e: Throwable){
            log("wsConnection: error in handling text ${e.message}")
        }
    }

    private suspend fun onWsConnectionOpened(){
        connected = true

        // If run backend and frontend on the
        // same PC, backend logic may not be executed yet (on my PC for example).
        // So, we can wait a little and try again.
        repeat(2){
            try{
                onConnectionOpened()
                return@repeat
            }
            catch(e: Throwable){
                log("error in handling WsConnectionOpened event: ${e.message}")
                delay(500)
            }
        }
    }

}