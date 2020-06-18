package viewModels

import api.Api
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import log
import mainScope
import kotlin.math.max

class WsConnection{

    private val initialDelayToConnect = 1000

    private val maxDelayToConnect = 10_000

    private var delayToConnect = 1000

    var connected = false
        private set

    var textHandler: suspend (String) -> Unit = {}

    var onConnectionOpened: suspend () -> Unit = {}

    private suspend fun handleText(text:String) = textHandler(text)

    fun openWsConnectionInfinite(){
        mainScope.launch{
            try {
                Api.subscriptions.openWsConnection {
                    log("ws connection is opened")
                    onWsConnectionOpened()
                    wsConnectionWork(incoming)
                }
                delayToConnect = initialDelayToConnect
            }
            catch(e:Throwable) {
                log("error during connecting to ws api.getEndpoint: ${e.message}")
                delayToConnect = max(delayToConnect * 2, maxDelayToConnect)
            }
            finally{
                log("ws connection is closed")
                connected = false
                delay(delayToConnect.toLong())
                openWsConnectionInfinite()
            }
        }
    }

    private suspend fun wsConnectionWork(incoming: ReceiveChannel<Frame>){

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