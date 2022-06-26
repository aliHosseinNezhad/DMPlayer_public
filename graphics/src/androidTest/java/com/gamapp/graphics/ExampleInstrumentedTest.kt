package com.gamapp.graphics

import android.text.TextPaint
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
const val TAG = "ExampleInstrumentedTest"
/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        val paint = TextPaint()
        paint.color = Color.White.toArgb()
        paint.textSize = 100f
        val s = paint.measureText("bebar ey barroon")
        Log.i(TAG, "useAppContext: $s")
        paint.reset()
        paint.textSize = 130f
        paint.color = Color.Black.toArgb()
        val s1 = paint.measureText("bebar ey barroon")
        Log.i(TAG, "useAppContext: $s1")

    }
}