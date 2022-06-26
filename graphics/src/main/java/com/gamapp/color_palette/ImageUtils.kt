package com.gamapp.color_palette

import android.graphics.Bitmap


val Bitmap.pixels
    get() = run {
        val intArray = IntArray(width * height)
        getPixels(intArray, 0, width, 0, 0, width, height)
        intArray
    }


fun IntArray.exit(){

}


class Pixels(val width: Int, val height: Int) {
    val colors = mutableMapOf<Int, ColorAverageHolder>()
    private var intArray = IntArray(width * height) {
        -1
    }
    private var originalIntArray: IntArray? = null

    fun setOriginalIntArray(intArray: IntArray) {
        originalIntArray = intArray
    }

    constructor(width: Int, height: Int, intArray: IntArray) : this(width, height) {
        if (intArray.size == width * height)
            this.intArray = intArray
    }

    operator fun get(x: Int, y: Int): Int {
        return intArray[y * width + x]
    }

    operator fun IntArray.get(x: Int, y: Int): Int {
        return this[y * width + x]
    }

    operator fun set(x: Int, y: Int, value: Int) {
        intArray[y * width + x] = value
    }

}