package components.mainScreen

import components.GlobalStyle
import kotlinx.css.*
import react.*
import styled.css
import styled.styledDiv

external interface HeaderProps: RProps {
    var userName: String
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
            }
            styledDiv{
                css{
                    float = kotlinx.css.Float.left
                    fontSize = GlobalStyle.headerFontSize
                    textAlign = TextAlign.center
                    padding = "14px 16px"
                }
                +"Cross Wars"
            }
            styledDiv{
                css{
                    float = kotlinx.css.Float.right
                    textAlign = TextAlign.center
                    padding = "14px 16px"
                    fontSize = GlobalStyle.headerFontSize
                }
                +"${props.userName}"
            }

        }
    }
}