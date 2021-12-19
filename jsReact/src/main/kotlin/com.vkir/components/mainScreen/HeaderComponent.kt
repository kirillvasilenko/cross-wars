package com.vkir.components.mainScreen

import com.vkir.components.GlobalStyle
import com.vkir.components.VMComponent
import com.vkir.components.VmProps
import com.vkir.viewModels.mainScreen.HeaderVm
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onMouseOutFunction
import kotlinx.html.js.onMouseOverFunction
import com.vkir.mainScope
import react.RBuilder
import react.dom.attrs
import styled.css
import styled.styledDiv

fun RBuilder.header(handler: VmProps<HeaderVm>.() -> Unit) {
    child(Header::class) {
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