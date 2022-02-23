import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

private val log = KotlinLogging.logger {}

enum class Screen {
    Login,
    Dashboards
}

class Navigator {

    private var _screen = mutableStateOf(Screen.Login)
    val screen: Screen by _screen

    fun navigateTo(newScreen: Screen) {
        _screen.value = newScreen
    }
}

fun main() {
    log.info { "Starting app" }
    log.info { "href: ${window.location.href}"}
    log.info { "pathname: ${window.location.pathname}"}
    var count: Int by mutableStateOf(0)
    val nav = Navigator()

    CoroutineScope(Dispatchers.Main).launch {
        for (i in 1..5) {
            delay(2000)
            window.history.pushState("page$i", "page$i", "/path$i")
        }
        for (i in 1..5) {
            log.info { "back" }
            delay(2000)
            window.history.back()
        }
    }
    renderComposable(rootElementId = "root") {
        when (nav.screen) {
            Screen.Login -> {
                Text("Auth screen")
                Button(attrs = {
                    onClick { nav.navigateTo(Screen.Dashboards) }
                }) {
                    Text("To Dashboard")
                }
            }

            Screen.Dashboards -> {
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