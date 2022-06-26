package com.gamapp.color_palette.model

import androidx.compose.ui.graphics.Color

sealed interface ColorReturn {
    class TwoColors(
        val c1: Color,
        val c2: Color
    ) : ColorReturn

    object EmptyColor : ColorReturn
}