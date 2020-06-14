
import components.app
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import react.dom.render
import viewModels.AppVm
import kotlin.browser.document

val mainScope = MainScope()


fun main() {
    val appVm = AppVm()
    render(document.getElementById("root")) {
        app{
            pVm = appVm
        }
    }
}
