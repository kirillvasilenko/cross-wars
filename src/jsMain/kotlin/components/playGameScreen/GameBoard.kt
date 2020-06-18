package components.playGameScreen

import components.GlobalStyle
import components.VMComponent
import components.VmProps
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import mainScope
import react.RBuilder
import react.ReactElement
import styled.css
import styled.styledDiv
import viewModels.playGameScreen.BoardFieldVm
import viewModels.playGameScreen.GameBoardVm

fun RBuilder.gameBoard(handler: VmProps<GameBoardVm>.() -> Unit): ReactElement {
    return child(GameBoard::class) {
        this.attrs(handler)
    }
}

class GameBoard(props: VmProps<GameBoardVm>): VMComponent<GameBoardVm>(props) {

    override fun RBuilder.render() {
        styledDiv {
            css{
                boxSizing = BoxSizing.borderBox
                height = 100.vh
                width = 100.vh
                display = Display.grid

                val columns = vm.board.map{ 1.fr }.joinToString(" ")
                gridTemplateColumns = GridTemplateColumns(columns)
                val rows = vm.board[0].map{ 1.fr }.joinToString(" ")
                gridTemplateRows = GridTemplateRows(rows)

            }

            vm.board.forEach { row ->
                row.forEach { fieldVm ->
                    boardField { pVm = fieldVm }
                }
            }
        }
    }
}

fun RBuilder.boardField(handler: VmProps<BoardFieldVm>.() -> Unit): ReactElement {
    return child(BoardField::class) {
        this.attrs(handler)
    }
}

class BoardField(props: VmProps<BoardFieldVm>): VMComponent<BoardFieldVm>(props) {

    override fun RBuilder.render() {
        styledDiv {
            css{
                borderWidth = GlobalStyle.borderWidth / 2
                borderColor = GlobalStyle.mainColor
                if(vm.x != 0){
                    borderTopStyle = BorderStyle.solid
                }
                if(vm.x != 9){
                    borderBottomStyle = BorderStyle.solid
                }
                if(vm.y != 0){
                    borderLeftStyle = BorderStyle.solid
                }
                if(vm.y != 9){
                    borderRightStyle = BorderStyle.solid
                }

                height = LinearDimension.available
                width = LinearDimension.available

                display = Display.flex
                justifyContent = JustifyContent.center
                alignItems = Align.center
                overflow = Overflow.hidden

                if(vm.canExecuted){
                    cursor = Cursor.pointer
                }
            }

            attrs{
                if(vm.canExecuted){
                    onClickFunction = {
                        mainScope.launch {
                            vm.execute()
                        }
                    }
                }
            }

            if (vm.currentState != null) {
                userInGameSymbol {
                    pVm = vm.currentState!!
                }
            }
        }
    }
}