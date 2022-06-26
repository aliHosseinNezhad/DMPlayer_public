package com.gamapp.color_palette

import android.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.gamapp.color_palette.model.ColorAndCount
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.withSign

val Int.red get() = Color.red(this)
val Int.green get() = Color.green(this)
val Int.blue get() = Color.blue(this)
val Int.alpha get() = Color.alpha(this)

fun Float?.normalize(step: Float = 1f): Float? {
    if (this == null) return null
    return this - this % step
}

fun List<androidx.compose.ui.graphics.Color>.average(): Int {
    var alpha: Long = 0
    var red: Long = 0
    var green: Long = 0
    var blue: Long = 0
    var count: Long = 0
    forEach {
        val color = it.toArgb()
        red += color.red
        green += color.green
        blue += color.blue
        alpha += color.alpha
        count++
    }
    alpha = (alpha / count.toFloat()).toLong()
    red = (red / count.toFloat()).toLong()
    green = (green / count.toFloat()).toLong()
    blue = (blue / count.toFloat()).toLong()
    return Color.argb(alpha.toInt(), red.toInt(), green.toInt(), blue.toInt())
}


//
internal fun absResponse(x: Double, a: Double, b: Double, c: Double, d: Double, g: Double):
        Double {
    return response(if (x < 0.0) -x else x, a, b, c, d, g).withSign(x)
}

internal fun response(x: Double, a: Double, b: Double, c: Double, d: Double, g: Double):
        Double {
    return if (x >= d) (a * x + b).pow(g) else c * x
}

fun eotf(x: Double): Double {
    return absResponse(
        x,
        1 / 1.055,
        0.055 / 1.055,
        1 / 12.92,
        0.04045,
        2.4
    )
}

private fun saturate(v: Float): Float {
    return if (v <= 0.0f) 0.0f else (if (v >= 1.0f) 1.0f else v)
}

fun Int.luminance(): Float {
    val r = eotf(red.toDouble() / 255)
    val g = eotf(green.toDouble() / 255)
    val b = eotf(blue.toDouble() / 255)
    return saturate(((0.2126 * r) + (0.7152 * g) + (0.0722 * b)).toFloat())
}

fun Int.saturation(): Float {
    val min = min(red, min(green, blue)) / 255f
    val max = max(red, max(green, blue)) / 255f
    return if (max == 0f) 0f else (max - min) / max
}

fun Int.hue(): Float? {
    val max = maxOf(maxOf(red, green), blue)
    val min = minOf(minOf(red, green), blue)
    val df = (max - min).toFloat()
    if (df == 0f) return null
    val hue = when {
        (red == max) -> (green - blue) / df
        (green == max) -> 2f + (blue - red) / df
        (blue == max) -> 4f + (red - green) / df
        else -> null
    }?.let {
        if (it < 0) (360 + it * 60) else it * 60
    }
    return hue
}




fun List<ColorAndCount>.combine(range: Pair<Int, Int>): Int? {
    val r1 = range.first
    val r2 = range.second - 1
    var r = 0
    var g = 0
    var b = 0
    var a = 0
    var count = 0
    if (r2 >= r1) {
        for (i in r1..r2) {
            count += this[i].count
            r += this[i].color.red * this[i].count
            g += this[i].color.green * this[i].count
            b += this[i].color.blue * this[i].count
            a += this[i].color.alpha * this[i].count
        }
        r /= count
        g /= count
        b /= count
        a /= count
        return android.graphics.Color.argb(a, r, g, b)
    } else {
        return null
    }
}