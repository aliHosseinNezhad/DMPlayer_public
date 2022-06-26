package com.gamapp.dynamicgradient

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Constraints
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import java.util.concurrent.Executors
import kotlin.math.*

const val TAG = "DynamicGradientUpdate"


object DynamicGradient {
    const val GradientThree = "gradientThree"
    const val GradientOne = "gradientOne"
    const val GradientTwo = "gradientTwo"
}

internal const val Duration = 8000

class DynamicGradientState {
    private val animatable = Animatable(0f)
    internal val placeables = mutableMapOf<Any?, Placeable>()
    internal val colorOne = mutableStateOf(Color(0, 100, 255))
    internal val colorTwo = mutableStateOf(Color.Magenta)
    internal val colorThree = mutableStateOf(Color.Magenta)


    internal val radius = mutableStateOf(50f)
    internal val readRadius get() = radius.value
    internal var fixedCircleRadius by mutableStateOf(50)
    internal val readFixedCircleRadius get() = fixedCircleRadius
    internal val center = mutableStateOf(Offset.Zero)
    internal val translation = mutableStateOf(Offset.Zero)

    private var resumed by mutableStateOf(false)
    private var enabledByUser by mutableStateOf(false)
    internal var count: Float = 0f

    internal var paint1 by mutableStateOf(null as Paint?)
    internal var paint2 by mutableStateOf(null as Paint?)

    @Composable
    internal fun Init() {
        val lifecycle = LocalLifecycleOwner.current.lifecycle
        LaunchedEffect(key1 = lifecycle) {
            lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    resumed = source.lifecycle.currentState == Lifecycle.State.RESUMED
                }
            })
        }
        LaunchedEffect(Unit) {
            val enabled by derivedStateOf {
                enabledByUser && resumed
            }
            launch {
                snapshotFlow {
                    enabled
                }.collectLatest {
                    if (it)
                        animation()
                }
            }
            launch {
                snapshotFlow {
                    radius.value
                }.collectLatest {
                    val v = count
                    val cx1 = radius.value / 2f * cos(PI * 2f * v).toFloat()
                    val cy1 = radius.value / 2f * sin(2 * PI * 2f * v).toFloat() / 2f
                    translation.value = Offset(cx1, cy1)
                }
            }
        }
    }

    private fun check() = enabledByUser && resumed

    private inline fun Animatable<Float, AnimationVector1D>.frame(cancel: () -> Unit) {
        val v = value
        count = v
        val cx1 = radius.value / 2f * cos(PI * 2f * v).toFloat()
        val cy1 =
            radius.value / 2f * sin(2 * PI * 2f * v).toFloat() / 2f
        translation.value = Offset(cx1, cy1)
        if (!(check())) {
            cancel()
            return
        }
    }

    internal suspend fun animation() {
        val gradientCoroutineContext = Executors.newSingleThreadExecutor()
            .asCoroutineDispatcher()
        withContext(gradientCoroutineContext) {
            if (count != 0f && check()) {
                val duration = ((1 - count) * Duration).toInt()
                animatable.snapTo(count)
                animatable.animateTo(1f, animationSpec = tween(duration, easing = LinearEasing)) {
                    frame {
                        return@animateTo
                    }
                }
            }
            if (check()) {
                animatable.snapTo(0f)
                animatable.animateTo(
                    1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            Duration,
                            easing = LinearEasing
                        )
                    )
                ) {
                    frame {
                        return@animateTo
                    }
                }
            }
        }
        try {
            gradientCoroutineContext.close()
        } catch (e: Exception) {
        }
    }

    fun start() {
        enabledByUser = (true)
    }

    fun stop() {
        enabledByUser = (false)
    }

    fun set(enable: Boolean) {
        enabledByUser = enable
    }


    private suspend fun makePaint(color: Color): Paint {
        return Paint().apply {
            withContext(Dispatchers.Default) {
                isAntiAlias = true
                shader = RadialGradientShader(
                    center = center.value,
                    radius = radius.value,
                    colors = color.generateColors(radius.value)
                )
            }
        }
    }

    suspend fun setColors(color1: Color, color2: Color) {
        paint1 = makePaint(color1)
        paint2 = makePaint(color2)
    }


    private fun normal(input: Float, d: Float): Float {
        val cos = cos(input * PI / 2 * d).toFloat()
        val exp = exp(-input * input)
        return exp * cos
    }

    private fun Color.generateColors(radius: Float): List<Color> {
        val colors = mutableListOf<Color>()
        val r = radius.toInt()
        var cr = 0
        val d = 0.7f
        val bAlpha = alpha
        while (cr < r) {
            val s = cr / r.toFloat()
            val alpha = normal(s * 1 / d, d) * bAlpha
            colors += copy(alpha.coerceIn(0f, 1f))
            cr++
        }
        return colors.toList()
    }
}

@Composable
internal fun dynamicGradientMeasurePolicy(state: DynamicGradientState) = remember {
    MeasurePolicy { measurables, constraints ->
        val h = constraints.maxHeight
        val w = constraints.maxWidth
        val bound = (min(w, h) * 1.5f).toInt()
        val radius = bound / 2f
        val const = Constraints.fixed(bound, bound)
        val item1 =
            measurables.firstOrNull { it.layoutId == DynamicGradient.GradientOne }?.measure(const)
        val item2 =
            measurables.firstOrNull { it.layoutId == DynamicGradient.GradientTwo }?.measure(const)
        layout(w, h) {
            if (state.readRadius != radius) {
                state.radius.value = radius
                state.center.value = Offset(radius, radius)
            }
            item2?.placeWithLayer((w - bound) / 2, (h - bound * 0.78f).toInt()) {
                val t = state.translation.value
                translationX = -t.x
                translationY = -t.y
                alpha = 0.8f
            }
            item1?.placeWithLayer((w - bound) / 2, (h - bound * 0.78f).toInt()) {
                val t = state.translation.value
                translationX = t.x
                translationY = t.y
                alpha = 0.8f
            }
        }
    }
}

@Composable
fun rememberDynamicGradientState() = remember {
    DynamicGradientState()
}

val Size.radius
    get() = run {
        max(width, height)
    }

@Composable
fun DynamicGradient(
    state: DynamicGradientState,
    modifier: Modifier
) {
    val measurePolicy = dynamicGradientMeasurePolicy(state = state)
    state.Init()
    Layout(measurePolicy = measurePolicy, modifier = modifier, content = {
        Box(
            modifier = Modifier
                .layoutId(DynamicGradient.GradientOne)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawIntoCanvas {
                    if (state.paint1 != null)
                        it.drawCircle(
                            center = center,
                            radius = state.radius.value,
                            paint = state.paint1!!
                        )
                }
            }
        }
        Box(
            modifier = Modifier
                .layoutId(DynamicGradient.GradientTwo)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawIntoCanvas {
                    if (state.paint2 != null)
                        it.drawCircle(
                            center = center,
                            radius = state.radius.value,
                            paint = state.paint2!!
                        )
                }
            }
        }
    })
}