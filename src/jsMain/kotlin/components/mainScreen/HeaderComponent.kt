package components.mainScreen

import components.GlobalStyle
import kotlinx.css.*
import react.*
import styled.css
import styled.styledDiv
import viewModels.common.CommandVm

external interface HeaderProps: RProps {
    var userName: String
}

class LogoutVm: CommandVm(){
    override suspend fun executeImpl() {
        TODO("Not yet implemented")
    }

}

fun RBuilder.header(handler: HeaderProps.() -> Unit): ReactElement {
    return child(Header::class) {
        this.attrs(handler)
    }
}

class Header: RComponent<HeaderProps, RState>() {
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
                }
                +props.userName
            }

        }
    }
}