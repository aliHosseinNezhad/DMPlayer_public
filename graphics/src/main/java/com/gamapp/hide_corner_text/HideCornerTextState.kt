package com.gamapp.hide_corner_text

import android.text.TextPaint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.math.min

const val MoveDuration = 15

sealed class ShaderSide {
    object Both : ShaderSide()
    object Right : ShaderSide()
    object Left : ShaderSide()
    object None : ShaderSide()
}

class HideCornerTextState {
    internal var paint = TextPaint().apply {
        isAntiAlias = true
    }
    internal var count by mutableStateOf(0)

    private fun initPaint(color: Color, width: Float, height: Float) {
//        paint.asFrameworkPaint().color = color.toArgb()
//        paint.asFrameworkPaint().textSize = min(width, height) * 0.8f
    }

    internal val animatable: Animatable<Float, AnimationVector1D> = Animatable(0f)


    internal suspend fun animation(
        text: String,
        color: Color,
        width: Float,
        height: Float
    ) {
        val coroutineDispatcher = Executors.newSingleThreadExecutor()
            .asCoroutineDispatcher()
        withContext(coroutineDispatcher) {
            val textWidth = paint
                .measureText(text)
            val dc = (width / 500).toInt().coerceAtLeast(1)
            val check = textWidth > width
            if (check)
                while (isActive) {
                    count = 0
                    rightShader(color, width, height)
                    delay(1500)
                    val time1 = ((textWidth / dc).toInt() * MoveDuration).coerceAtLeast(0)
                    bothShader(color, width, height)
                    animatable.snapTo(0f)
                    if (isActive && time1 != 0) {
                        animatable.animateTo(
                            -textWidth,
                            animationSpec = tween(time1, easing = LinearEasing)
                        ) {
                            if (!isActive) return@animateTo
                            count = value.toInt()
                        }
                    }
                    animatable.snapTo(width / 4)
                    val time2 = (width / (4 * dc)).toInt().coerceAtLeast(0) * MoveDuration
                    rightShader(color, width, height)
                    if (isActive && time2 != 0)
                        animatable.animateTo(
                            0f,
                            animationSpec = tween(time2, easing = LinearEasing)
                        ) {
                            count = value.toInt()
                            if (!isActive) return@animateTo
                        }
                }
        }
        try {
            coroutineDispatcher.close()
        } catch (e: Exception) {
        }
    }

    private fun bothShader(color: Color, width: Float, height: Float) {
        doShader(ShaderSide.Both, color, width, height)
    }

    private fun rightShader(color: Color, width: Float, height: Float) {
        doShader(ShaderSide.Right, color, width, height)
    }

    private fun leftShader(color: Color, width: Float, height: Float) {
        doShader(shaderSide = ShaderSide.Left, color, width, height)
    }

    private fun doShader(shaderSide: ShaderSide, color: Color, width: Float, height: Float) {
        val start = Offset(0f, height / 2)
        val end = Offset(width, height / 2)
        paint.isAntiAlias = true
        when (shaderSide) {
            is ShaderSide.Left -> {
                paint.shader = LinearGradientShader(
                    start,
                    end,
                    colors = listOf(
                        Color.Transparent,
                        color,
                        color
                    ),
                    colorStops = listOf(
                        0f,
                        0.1f,
                        1f
                    )
                )
            }
            is ShaderSide.Both -> {
                paint.shader = LinearGradientShader(
                    start,
                    end,
                    colors = listOf(
                        Color.Transparent,
                        color,
                        color,
                        Color.Transparent
                    ),
                    colorStops = listOf(
                        0f,
                        0.1f,
                        0.9f,
                        1f
                    )
                )
            }
            is ShaderSide.None -> {
                paint.shader = LinearGradientShader(
                    start,
                    end,
                    colors = listOf(
                        color,
                        color
                    ),
                    colorStops = listOf(
                        0f,
                        1f
                    )
                )
            }
            is ShaderSide.Right -> {
                paint.shader = LinearGradientShader(
                    start,
                    end,
                    colors = listOf(
                        color,
                        color,
                        Color.Transparent
                    ),
                    colorStops = listOf(
                        0f,
                        0.9f,
                        1f
                    )
                )
            }
        }
    }


    internal fun init(text: String, color: Color, width: Float, height: Float, center: Boolean) {
        paint.reset()
        paint.textSize = min(width, height) * 0.8f
        count = if (paint.measureText(text) > width) {
            rightShader(color, width, height)
            0
        } else {
            doShader(ShaderSide.None, color, width, height)
            if (center) {
                (width - paint.measureText(text)).toInt() / 2
            } else 0
        }
    }

    suspend fun stop(text: String, color: Color, width: Float, height: Float, center: Boolean) {
        animatable.stop()
        init(text, color, width, height, center)
    }
}

@Composable
fun rememberHideCornerState() = remember {
    HideCornerTextState()
}