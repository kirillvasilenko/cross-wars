
import components.app
import kotlinx.coroutines.MainScope
import react.dom.render
import viewModels.AppVm
import kotlin.browser.document

val mainScope = MainScope()

fun main() {
    render(document.getElementById("root")) {
        app{
            pVm = AppVm()
        }
    }
}
