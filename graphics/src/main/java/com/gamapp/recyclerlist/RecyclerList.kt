package com.gamapp.recyclerlist

import android.util.Log
import androidx.compose.animation.SplineBasedFloatDecayAnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.generateDecayAnimationSpec
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.util.fastForEach
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.math.abs
import kotlin.math.min
import kotlin.random.Random
import kotlin.random.asJavaRandom

const val TAG = "TAGTAGTAG"
fun random(v: Long) = Random(v).asJavaRandom()
fun List<Int>.generateColors(): List<Color> {
    val red = this.shuffled()
    val green = red.shuffled()
    val blue = green.shuffled()
    val colors = Array<Color?>(size) {
        null
    }
    var random: java.util.Random
    for (i in indices) {
        val r = red[i]
        val g = green[i]
        val b = blue[i]
        random = random(r.toLong())
        val base = 10
        val rc = random.nextInt(256 - base) + base
        random = random(g.toLong())
        val gc = random.nextInt(256 - base) + base
        random = random(b.toLong())
        val bc = random.nextInt(256 - base) + base
        colors[i] = Color(rc, gc, bc)
    }
    return colors.mapNotNull { it }
}

//@Composable
//fun <T> recyclerColumnMeasurePolicy(state: RecyclerState<T>) = remember {
//    MeasurePolicy { measurables, constraints ->
//        with(state) {
////            placeItems(measurables, constraints)
//        }
//    }
//}



class Projection<T>(
    val i: Int,
    var value: T? = null,
    var h: Int = 0,
    var index: Int = 0,
    var placeable: Placeable? = null,
)

class RecyclerState<T> {
    var state = SubcomposeLayoutState()
    internal val _scroll: MutableState<Int> = mutableStateOf(0)
    val scroll: State<Int> = _scroll
    private val projections = mutableListOf<Projection<T>>()
    val visibleCount: Int get() = projections.size
    var dh: Int = 0
    var h: Int = 0
    var screenHeight: Int = 0
    internal var content: @Composable ((T) -> Unit)? = null
        set(value) {
            field = value
            cached = null
        }
    var items = listOf<T>()
    val min = mutableStateOf(0f)
    val max = mutableStateOf(1f)

    @Composable
    private fun Content(item: T?) {
        Box(modifier = Modifier.wrapContentSize()) {
            if (item != null && content != null) {
                content?.let {
                    it(item)
                }
            }
        }
    }

    var cached: Placeable? = null
    internal fun SubcomposeMeasureScope.initProjection(
        items: List<T>,
        constraints: Constraints
    ) {
        val placeable = if (cached == null) {
            state.precompose(slotId = "45") {
                Content(items.first())
            }
            cached = subcompose("45") {
                Content(items.first())
            }.first().measure(constraints.copy(minWidth = 0, minHeight = 0))
            cached!!
        } else cached!!
        h = placeable.height
        screenHeight = constraints.maxHeight
        val n = if (h >= screenHeight) 3 else ((h + screenHeight) / h + 2)
        Log.i(TAG, "count: $n h:$h screenHeight:$screenHeight")
        dh = ((n - 1) * h - screenHeight) / 2
        val count = min(n, items.size)
        max.value = items.lastIndex * h.toFloat()
        if (count != projections.size) {
            projections.clear()
            for (i in (0 until count)) {
                projections += Projection<T>(i = i).apply {
                    this.value = null
                }
            }
        }
    }

    internal fun SubcomposeMeasureScope.subcomposeItems(constraints: Constraints) {
        projections.fastForEach { projection ->
            val measurable = subcompose(projection.i) {
            val value = projection.value
            Content(value)
        }.first()
            if (projection.placeable == null){
                projection.placeable = measurable.measure(constraints)
            }
        }
    }

    fun MeasureScope.placeItems(constraints: Constraints): MeasureResult {
        val h = h
        val s = scroll.value
        val n = visibleCount
        val dh = dh
        projections.forEach {
            val i = it.i
            val k = if (s >= -n * h + (i) * h + dh) {
                (s + n * h - i * h - dh) / (n * h)
            } else {
                (s + n * h - i * h - dh) / (n * h) - 1
            }
            it.h = k * n * h + (i - 1) * h
            it.index = k * n + i - 1
        }
        return layout(constraints.maxWidth, constraints.maxHeight) {
            projections.forEach {
                if (it.index in items.indices)
                    it.placeable?.placeWithLayer(0, 0, layerBlock = {
                        translationY = (it.h - s).toFloat()
                    })
            }.also {
                projections.fastForEach {
                    val value = items.item(it.index)
                    if (value != it.value) {
                        it.value = value
                        it.placeable = null
                    }
                }
            }
        }
    }

    fun scrollBy(ds: Float): Float {
        val s = _scroll.value + ds
        _scroll.value = (s).coerceIn(min.value, max.value).toInt()
        val df = s - _scroll.value
        return ds - df
    }
}

fun indexOf(index: Int, size: Int): Int {
    val i = index % size
    println(i)
    return if (i < 0) {
        size + i
    } else {
        i
    }
}

private fun <E> List<E>.item(index: Int): E? {
    return this[indexOf(index, size)]
}

internal fun Offset.get(vertical: Boolean): Float {
    return if (vertical) y else x
}

internal fun Velocity.get(vertical: Boolean): Float {
    return if (vertical) y else x
}

fun Job?.stop() {
    try {
        this?.cancel()
    } catch (e: Exception) {
    }
}

@androidx.compose.runtime.Composable
fun <T> RecyclerList(
    items: List<T>,
    content: @Composable (T) -> Unit,
    modifier: Modifier,
    isVertical: Boolean = true
) {
    val recyclerState = remember {
        RecyclerState<T>()
    }
    var flingJob = remember<Job?> {
        null
    }
    val density = LocalDensity.current
    val flingDecay = remember {
        SplineBasedFloatDecayAnimationSpec(density).generateDecayAnimationSpec<Float>()
    }
//    val measurePolicy = recyclerColumnMeasurePolicy(state = recyclerState)
    val scrollModifier = Modifier.pointerInput(isVertical) {
        coroutineScope {
            val velocityTracker = VelocityTracker()
            flingJob.stop()
            detectDragGestures(onDragEnd = {
                val velocity = velocityTracker.calculateVelocity()
                flingJob.stop()
                flingJob = launch {
                    velocity.doFling(isVertical = isVertical, flingDecay) {
                        recyclerState.scrollBy(-it)
                    }
                }
                //do fling heart here
                velocityTracker.resetTracking()
            }, onDragStart = {
                flingJob.stop()
            }) { change, dragAmount ->
                velocityTracker.addPointerInputChange(change)
                //scroll here
                val ds = -dragAmount.get(isVertical)
                recyclerState.scrollBy(ds)
            }
        }
    }
    SubcomposeLayout(
        measurePolicy = {
            if (it.isZero) return@SubcomposeLayout layout(it.maxWidth, it.maxHeight) {}
            if (items.isEmpty()) return@SubcomposeLayout layout(it.maxWidth, it.maxHeight) {}
            with(recyclerState) {
                this.content = content
                this.items = items
                initProjection(items = items, constraints = it)
                subcomposeItems(it.copy(minWidth = 0, minHeight = 0))
                placeItems(it)
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .clip(RectangleShape)
            .then(scrollModifier),
        state = recyclerState.state
    )
}

private suspend fun Velocity.doFling(
    isVertical: Boolean,
    flingDecay: DecayAnimationSpec<Float>,
    scrollBy: (Float) -> Float
) {
    var velocityLeft = get(isVertical)
    var lastValue = 0f
    AnimationState(
        initialValue = 0f,
        initialVelocity = get(isVertical),
    ).animateDecay(flingDecay) {
        val delta = value - lastValue
        val consumed = scrollBy(delta)
        lastValue = value
//        velocityLeft = this.velocity
//         avoid rounding errors and stop if anything is unconsumed
//        if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
    }
}
