package com.vkir.components

import com.vkir.model.SideOfTheForce
import com.vkir.viewModels.LoginVm
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.html.InputType
import kotlinx.html.classes
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.spellCheck
import com.vkir.mainScope
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import react.RBuilder
import react.dom.attrs
import react.dom.option
import styled.*

fun RBuilder.loginForm(handler: VmProps<LoginVm>.() -> Unit) {
    child(LoginForm::class) {
        this.attrs(handler)
    }
}

class LoginForm(props: VmProps<LoginVm>) : VMComponent<LoginVm>(props) {

    override fun RBuilder.render() {
        styledDiv{
            css {
                height = LinearDimension.fillAvailable
                width = LinearDimension.fillAvailable

                display = Display.flex
                justifyContent = JustifyContent.center
                alignItems = Align.center
            }
            styledDiv{
                css{
                    display = Display.grid
                    gridTemplateColumns = GridTemplateColumns(1.fr, 1.fr)
                    gridTemplateRows = GridTemplateRows(1.fr, 1.fr, 1.fr, 1.fr, 1.fr, 1.fr, 1.fr)
                }
                styledP{
                    css{
                        margin = "auto"
                        gridColumn = GridColumn("span 2")
                        gridRow = GridRow("span 3")
                        fontSize = GlobalStyle.logoFontSize
                    }
                    +"Cross Wars"
                }

                styledP{
                    css{
                        margin = GlobalStyle.margin
                    }
                    +"Your name tell young programan"
                }
                styledInput{
                    css{
                        margin = GlobalStyle.margin
                        color = GlobalStyle.mainColor
                    }
                    attrs{
                        type = InputType.text
                        autoFocus = true
                        spellCheck = false
                        placeholder = "Tolya Vader"
                        onChangeFunction = {
                            vm.name = (it.target as HTMLInputElement).value
                        }
                    }
                }

                styledP{
                    css{
                        margin = GlobalStyle.margin
                    }
                    +"Side of the Force choose"
                }
                styledSelect{
                    css{
                        margin = GlobalStyle.margin
                        color = GlobalStyle.mainColor
                        backgroundColor = GlobalStyle.backgroundColor
                    }
                    attrs{
                        onChangeFunction = {
                            vm.sideOfTheForce = SideOfTheForce.valueOf((it.target as HTMLSelectElement).value)
                        }
                    }
                    option{
                        attrs{
                            value = SideOfTheForce.Light.toString()
                        }
                        +"${SideOfTheForce.Light}"
                    }
                    option{
                        attrs{
                            value = SideOfTheForce.Dark.toString()
                        }
                        +"${SideOfTheForce.Dark}"
                    }
                }
                styledP{
                    css{
                        margin = GlobalStyle.margin
                    }
                    +"Sword color (girls like it)"
                }
                styledSelect{
                    css{
                        margin = GlobalStyle.margin
                        color = getUserColor(vm.sideOfTheForce, vm.color)
                        backgroundColor = GlobalStyle.backgroundColor
                    }
                    attrs{
                        onChangeFunction = {
                            vm.color = (it.target as HTMLSelectElement).value.toInt()
                        }
                    }
                    (0..2).forEach { colorId ->
                        styledOption{
                            css{
                                color = getUserColor(vm.sideOfTheForce, colorId)
                            }
                            attrs{
                                value = colorId.toString()
                            }

                            +"${getUserColor(vm.sideOfTheForce, colorId)}"
                        }
                    }
                }

                styledButton{
                    css{
                        margin = GlobalStyle.margin
                        gridColumn = GridColumn("span 2")
                    }
                    attrs{
                        classes=setOf("button")
                        if(!vm.canExecuted){
                            disabled = true
                        }

                        onClickFunction = {
                            mainScope.launch {
                                vm.execute()
                            }
                        }
                    }
                    +"Sign up!"
                }
            }
        }
    }
}