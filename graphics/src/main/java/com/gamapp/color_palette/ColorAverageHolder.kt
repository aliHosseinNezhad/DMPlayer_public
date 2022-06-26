package com.gamapp.color_palette

import androidx.compose.ui.graphics.Color

class ColorAverageHolder(
    var sRed: Long = 0,
    var sGreen: Long = 0,
    var sBlue: Long = 0,
    var sAlpha: Long = 0,
    var count: Int = 0,
)

fun ColorAverageHolder.toIntColor(): Int {
    val red = this.let {
        try {
            (it.sRed / (it.count).toFloat()).toInt()
        } catch (e: Exception) {
            0
        }
    }
    val blue = this.let {
        try {
            (it.sBlue / (it.count).toFloat()).toInt()
        } catch (e: Exception) {
            0
        }
    }
    val green = this.let {
        try {
            (it.sGreen / (it.count).toFloat()).toInt()
        } catch (e: Exception) {
            0
        }
    }
    val alpha = this.let {
        try {
            (it.sAlpha / (it.count).toFloat()).toInt()
        } catch (e: Exception) {
            0
        }
    }
    return android.graphics.Color.argb(alpha, red, green, blue)
}

fun ColorAverageHolder.toColor(): Color {
    return Color(
        toIntColor()
    )
}

operator fun ColorAverageHolder.plus(colorAverage: ColorAverageHolder): ColorAverageHolder {
    val c1 = this
    return ColorAverageHolder(
        sRed = c1.sRed + colorAverage.sRed,
        sGreen = c1.sGreen + colorAverage.sGreen,
        sBlue = c1.sBlue + colorAverage.sBlue,
        sAlpha = c1.sRed + colorAverage.sRed,
        count = c1.count + colorAverage.count,
    )
}