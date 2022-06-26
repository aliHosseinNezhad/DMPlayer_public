package com.gamapp.dmplayer.presenter.ui.theme

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.Rgb
import androidx.compose.ui.graphics.luminance

val primary = Color(0xFF0082FF)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)
val gray = Color(0xFF888888)
val white = Color(0xFFFFFFFF)
val secondary = Color.Magenta



val dark = Color(0xFF171719)
val dark2 = Color(0xFF000004)
val c = Color(0xFF444444)
val light = Color(0xFFF5F5F5)
val highLight = Color(0xFFFeFeFe)


val Colors.content get() = run { if (this.isLight) Color.LightGray else Color.DarkGray }
val Colors.dialog get() = run { if (isLight) Color(240, 240, 240) else Color(37, 37, 37) }
val Colors.onDialog get() = run { if (isLight) Color.DarkGray else Color.LightGray }
val Colors.onContent get() = run { if (this.isLight) Color.DarkGray else Color.Gray }
val Colors.popupColor get() = run { if (this.isLight) light else Color.DarkGray }
val Colors.onSelectionTextColor
    get() = run {
        if (this.isLight) Color(0xFF004281) else Color(
            0xFF0082FF
        )
    }