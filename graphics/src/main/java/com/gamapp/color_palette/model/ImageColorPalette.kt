package com.gamapp.color_palette.model


class ColorAndCount(val color: Int, val count: Int)
interface ImageColorPalette {
    val count: Int
    val saturationAverage: Float
    val luminanceAverage: Float
    val countAverage: Int

    /**
     * pair of color and count
     * */
    val colors: List<ColorAndCount>
}

class ImageColorPaletteImpl : ImageColorPalette {
    override var countAverage: Int = 0
    override var count: Int = 0
    override var saturationAverage: Float = 0f
    override var luminanceAverage: Float = 0f
    override val colors: MutableList<ColorAndCount> = mutableListOf()
}