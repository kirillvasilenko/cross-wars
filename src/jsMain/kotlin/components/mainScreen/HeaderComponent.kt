package components.mainScreen

import components.GlobalStyle
import components.VMComponent
import components.VmProps
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onMouseOutFunction
import kotlinx.html.js.onMouseOverFunction
import mainScope
import react.RBuilder
import react.ReactElement
import styled.css
import styled.styledDiv
import viewModels.mainScreen.HeaderVm

fun RBuilder.header(handler: VmProps<HeaderVm>.() -> Unit): ReactElement {
    return child(Header::class) {
        this.attrs(handler)
    }
}

class Header(props: VmProps<HeaderVm>): VMComponent<HeaderVm>(props) {
    override fun RBuilder.render() {

        styledDiv {
            css {
                overflow = Overflow.hidden
                margin = "8px 8px 0px 8px"
            }
            styledDiv{
                css{
                    float = Float.left
                    fontSize = GlobalStyle.headerFontSize
                    textAlign = TextAlign.center
                }
                +"Cross Wars"
            }
            styledDiv{
                css{
                    float = Float.right
                    textAlign = TextAlign.center
                    fontSize = GlobalStyle.headerFontSize
                    cursor = Cursor.pointer
                }
                attrs{
                    classes = setOf("flatButton")
                    onClickFunction = {
                        mainScope.launch {
                            vm.execute()
                        }
                    }
                    onMouseOverFunction = {
                        vm.mouseOver = true
                    }
                    onMouseOutFunction = {
                        vm.mouseOver = false
                    }
                }
                +vm.inscription
            }

        }
    }
}