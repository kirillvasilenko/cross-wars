
import Components.App
import kotlinx.coroutines.MainScope
import react.dom.render
import kotlin.browser.document
import kotlin.browser.window

val mainScope = MainScope()


fun main() {
    render(document.getElementById("root")) {
        child(App::class){}
    }
}
