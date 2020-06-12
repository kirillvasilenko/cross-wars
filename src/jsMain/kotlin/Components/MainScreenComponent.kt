package Components

import ViewModels.GameVm
import kotlinx.coroutines.launch
import mainScope
import model.UserDto
import react.*
import styled.styledDiv

external interface MainScreenProps: RProps {
    var user: UserDto
}


fun RBuilder.mainScreen(handler: MainScreenProps.() -> Unit): ReactElement {
    return child(MainScreen::class) {
        this.attrs(handler)
    }
}

class MainScreen: RComponent<MainScreenProps, RState>() {
    override fun RBuilder.render() {
        styledDiv {
            header{
                userName = props.user.name
            }
            games{
                games = listOf(GameVm(), GameVm(), GameVm(), GameVm())
                onStartNewGame = {
                    mainScope.launch {
                        val newGame = Api.games.startNewGame()
                    }
                }
            }
        }
    }
}