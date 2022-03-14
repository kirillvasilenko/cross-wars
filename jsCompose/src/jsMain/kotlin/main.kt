import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.vkir.svc.AuthSvc
import kotlinx.browser.window
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

private val log = KotlinLogging.logger {}

sealed class Screen {
    object Login: Screen()
    object Dashboard: Screen()
    data class Game(val gameId: String): Screen()
}

class Navigator {

    private var _screen = mutableStateOf<Screen>(Screen.Login)
    val screen: Screen by _screen

    fun navigateTo(newScreen: Screen) {
        _screen.value = newScreen
    }

    fun back() {

    }
}

class App {
    val authSvc = AuthSvc()

    val scope = CoroutineScope(SupervisorJob() + CoroutineName("hello") + CoroutineExceptionHandler { _,_ -> Unit })

    init {

        //authSvc.authenticated.collect
    }
}

fun main() {
    log.info { "Starting app" }
    log.info { "href: ${window.location.href}"}
    log.info { "pathname: ${window.location.pathname}"}
    var count: Int by mutableStateOf(0)
    val nav = Navigator()

    val app = App()



    /*CoroutineScope(Dispatchers.Main).launch {
        for (i in 1..5) {
            delay(2000)
            window.history.pushState("page$i", "page$i", "/path$i")
        }
        for (i in 1..5) {
            log.info { "back" }
            delay(2000)
            window.history.back()
        }
    }*/
    renderComposable(rootElementId = "root") {
        val authenticated = app.authSvc.authenticated.collectAsState()
        when (nav.screen) {
            Screen.Login -> {
                Text("Auth screen")
                Button(attrs = {
                    onClick { nav.navigateTo(Screen.Dashboard) }
                }) {
                    Text("To Dashboard")
                }
            }

            Screen.Dashboard -> {
                Text("Dashboard screen")
                Button(attrs = {
                    onClick { nav.navigateTo(Screen.Login) }
                }) {
                    Text("To Auth")
                }
            }
        }
        /*Div({ style { padding(25.px) } }) {
            Button(attrs = {
                onClick { count -= 1 }
            }) {
                Text("-")
            }

            Span({ style { padding(15.px) } }) {
                Text("$count")
                Text("epter")
            }

            Button(attrs = {
                onClick { count += 1 }
            }) {
                Text("+")
            }
        }*/
    }
}