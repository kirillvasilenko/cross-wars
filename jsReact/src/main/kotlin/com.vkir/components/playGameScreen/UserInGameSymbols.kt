package com.vkir.components.playGameScreen

import com.vkir.components.*
import com.vkir.viewModels.playGameScreen.UserInGameSymbolVm
import kotlinx.css.*
import react.RBuilder
import styled.css
import styled.styledSvg

fun RBuilder.userInGameSymbol(handler: VmProps<UserInGameSymbolVm>.() -> Unit) {
    child(UserInGameSymbol::class) {
        this.attrs(handler)
    }
}

class UserInGameSymbol(props: VmProps<UserInGameSymbolVm>): VMComponent<UserInGameSymbolVm>(props) {

    override fun RBuilder.render() {
        styledSvg {
            css{
                width = 100.pct
                height = 100.pct
                if(vm.aLittleHidden){
                    opacity = 0.2
                }
            }
            attrs["viewBox"] = "0 0 100 100"

            custom("filter"){
                attrs["id"] = "blur"
                custom("feGaussianBlur"){
                    attrs["stdDeviation"] = "1"
                }
            }

            when(vm.userSymbol % 4){
                0 -> crossSymbol(getUserColor(vm.sideOfTheForce, vm.swordColor), vm.glowable)
                1 -> circleSymbol(getUserColor(vm.sideOfTheForce, vm.swordColor), vm.glowable)
                2 -> rhombusSymbol(getUserColor(vm.sideOfTheForce, vm.swordColor), vm.glowable)
                3 -> plusSymbol(getUserColor(vm.sideOfTheForce, vm.swordColor), vm.glowable)
            }

        }
    }

}

fun RBuilder.circleSymbol(color: Color, glowable: Boolean = true, filter:String = "blur") {

    custom("circle") {
        if(glowable)
        {
            attrs["className"] = "glowable"
        }
        attrs["cx"] = 50
        attrs["cy"] = 50
        attrs["r"] = 40
        attrs["fill"] = "transparent"
        attrs["strokeWidth"] = GlobalStyle.symbolStrokeWidth
        attrs["stroke"] = color.toString()
        attrs["filter"] = "url(#$filter)"
    }

}

fun RBuilder.rhombusSymbol(color: Color, glowable: Boolean = true, filter:String = "blur") {

    custom("polyline") {
        if(glowable)
        {
            attrs["className"] = "glowable"
        }
        attrs["points"] = "10,50 50,10 90,50 50,90 10,50 50,10"
        attrs["fill"] = "transparent"
        attrs["strokeWidth"] = GlobalStyle.symbolStrokeWidth
        attrs["stroke"] = color.toString()
        attrs["filter"] = "url(#$filter)"
    }
}

fun RBuilder.plusSymbol(color: Color, glowable: Boolean = true, filter:String = "blur") {
    custom("polyline") {
        if(glowable)
        {
            attrs["className"] = "glowable"
        }
        attrs["points"] = "10,50 90,50 50,50 50,10 50,90"
        attrs["fill"] = "transparent"
        attrs["strokeWidth"] = GlobalStyle.symbolStrokeWidth
        attrs["stroke"] = color.toString()
        attrs["filter"] = "url(#$filter)"
    }
}

fun RBuilder.crossSymbol(color: Color, glowable: Boolean = true, filter:String = "blur") {

    custom("line") {
        if(glowable)
        {
            attrs["className"] = "glowable"
        }
        attrs["x1"] = 10
        attrs["y1"] = 10
        attrs["x2"] = 90
        attrs["y2"] = 90
        attrs["strokeWidth"] = GlobalStyle.symbolStrokeWidth
        attrs["stroke"] = color.toString()
        attrs["filter"] = "url(#$filter)"
    }
    custom("line") {
        if(glowable)
        {
            attrs["className"] = "glowable"
        }
        attrs["x1"] = 90
        attrs["y1"] = 10
        attrs["x2"] = 10
        attrs["y2"] = 90
        attrs["strokeWidth"] = GlobalStyle.symbolStrokeWidth
        attrs["stroke"] = color.toString()
        attrs["filter"] = "url(#$filter)"
    }

}
