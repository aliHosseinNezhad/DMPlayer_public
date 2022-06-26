package com.gamapp.color_palette

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.util.fastMaxBy
import androidx.core.graphics.scale
import com.gamapp.color_palette.model.ColorAndCount
import com.gamapp.color_palette.model.ColorReturn
import com.gamapp.color_palette.model.ImageColorPalette
import com.gamapp.color_palette.model.ImageColorPaletteImpl
import kotlin.math.min


private fun Float.getKey(range: IntRange): Int? {
    require(this in 0f..1f)
    val v = (this * 100).toInt() / 10
    return if (v in range) v else null
}

fun Bitmap.getPixels(): Pixels {
    val percent = height.toFloat() / width
    val mw = min(100, width)
    val mh = min((100 * percent).toInt(), height)
    return Pixels(mw, mh, scale(mw, mh).pixels)
}

fun Pixels.extractColors(
    colorMap: MutableMap<Int, ColorAverageHolder>,
    palette: ImageColorPaletteImpl
) {
    for (y in 0 until height)
        for (x in 0 until width) {
            val color = this[x, y]
            val luminance = color.luminance()
            val saturation = color.saturation()
            val hue = (color.hue() ?: continue).toInt()
            val luminanceKey = luminance.getKey(1..4) ?: continue
            val saturationKey = saturation.getKey(2..9) ?: continue
            val hueKey = hue / 10

            palette.saturationAverage += saturation
            palette.luminanceAverage += luminance
            palette.count++
            val key: Int = (hueKey) + (saturationKey * 100) + (luminanceKey * 1000)
            val colorAverage = colorMap.getOrPut(key) {
                ColorAverageHolder()
            }
            colorAverage.apply {
                sRed += color.red
                sGreen += color.green
                sBlue += color.blue
                sAlpha += color.alpha
                count += 1
            }
            colorMap[key] = colorAverage
        }
}

fun ImageColorPaletteImpl.setPaletteWithColorMap(colorMap: Map<Int, ColorAverageHolder>) {
    this.saturationAverage /= this.count.coerceAtLeast(1)
    this.luminanceAverage /= this.count.coerceAtLeast(1)
    var count = 0
    val colors = colorMap.values.map {
        count++
        countAverage += it.count
        ColorAndCount(color = it.toIntColor(), count = it.count)
    }
    if (colorMap.isEmpty()) count = 1
    countAverage /= count
    this.colors.addAll(colors)
}

fun Bitmap.createPalette(): ImageColorPalette {
    val palette = ImageColorPaletteImpl()
    val colorMap = mutableMapOf<Int, ColorAverageHolder>()
    val pixels = getPixels()
    pixels.extractColors(colorMap, palette)
    palette.setPaletteWithColorMap(colorMap)
    return palette
}

fun ImageColorPalette.filteredColors(): List<ColorAndCount> {
    return colors
        .filter { it.color.saturation() > saturationAverage * 0.6f && it.count > countAverage * 0.6f }
}

fun List<ColorAndCount>.groupByHue(): List<Pair<Int, Int>> {
    val group = hueGroup()
    val pairs = mutableListOf<Pair<Int, Int>>()
    if (group.size >= 2) {
        var count = 0
        var temp = group.first()
        count++
        while (count < group.size) {
            pairs += temp to group[count]
            temp = group[count]
            count++
        }
    }
    return pairs
}

fun List<Pair<Int, Int>>.sortByCount(colors: List<ColorAndCount>): List<Pair<Int, Int>> {
    return sortedByDescending { it ->
        colors.subList(it.first, it.second).sumOf { it.count }
    }
}


fun ImageColorPalette.getTwoColors(): ColorReturn {
    val colors = filteredColors().sortedBy { it.color.hue() }
    val groups = colors.groupByHue().sortByCount(colors)
    if (groups.isNotEmpty()) {
        if (groups.size == 1) {
            val c1 = colors.combine(groups[0]) ?: return ColorReturn.EmptyColor
            val c2 =
                colors.fastMaxBy { it.color.saturation() }?.color ?: return ColorReturn.EmptyColor
            return if (c1 == c2) ColorReturn.EmptyColor
            else ColorReturn.TwoColors(
                c1 = Color(c1),
                c2 = Color(c2)
            )
        } else {
            val c1 = colors.combine(groups[0]) ?: return ColorReturn.EmptyColor
            val c2 = colors.combine(groups[1]) ?: return ColorReturn.EmptyColor
            return ColorReturn.TwoColors(Color(c1), Color(c2))
        }
    } else return ColorReturn.EmptyColor
}

fun ImageColorPalette.getTwoOrDefaults(c1: Color, c2: Color): List<Color> {
    val colorReturn = getTwoColors()
    return if (colorReturn is ColorReturn.TwoColors) {
        listOf(colorReturn.c1, colorReturn.c2)
    } else listOf(
        c1,
        c2
    )
}


//list must be sorted by hue
fun List<ColorAndCount>.hueGroup(): List<Int> {
    fun Int.h() = this.hue()!!
    if (size < 2) return emptyList()
    var focus = 0
    val breaks = mutableListOf(focus)
    for (i in this.indices) {
        val h2 = this[i].color.h()
        val h1 = this[focus].color.h()
        if ((h2 - h1) > 5f) {
            focus = i
            breaks += i
        }
    }
    if (!breaks.contains(this.lastIndex)) {
        breaks += lastIndex
    }
    val b2 = hueGroup(breaks, 10f)
    val b3 = hueGroup(b2, 15f)
    val b4 = hueGroup(b3, 25f)
    return hueGroup(b4, 35f)

}

fun List<ColorAndCount>.hueGroup(breaks: List<Int>, delta: Float): List<Int> {
    fun Int.h() = this.hue()!!
    if (breaks.size <= 2) return breaks
    var focus = breaks.first()
    val newBreaks = mutableListOf(focus)
    for (i in breaks) {
        val h1 = this[focus].color.h()
        val h2 = this[i].color.h()
        if (h2 - h1 > delta) {
            newBreaks += i
            focus = i
        }
    }
    if (!newBreaks.contains(breaks.last())) {
        newBreaks += breaks.last()
    }
    return newBreaks
}




