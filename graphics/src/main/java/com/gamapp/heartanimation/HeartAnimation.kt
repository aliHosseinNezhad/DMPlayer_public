package com.gamapp.heartanimation

import android.content.Context
import android.util.Log
import androidx.annotation.FloatRange
import androidx.annotation.Keep
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.gamapp.graphics.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import java.io.BufferedReader
import java.io.InputStream
import java.util.*
import kotlin.math.*


val heartBound = 50.dp
val bound = 300.dp
const val TAG = "TESTHeart"

/**
 * [C]  : drag constant must be greater than zero
 * */
const val C = 0.8f


fun Offset.toPosition(bound: Float): Position = run {
    val x = x / bound
    val y = y / bound
    Position(x, y)
}


data class Position(
    @FloatRange(from = 0.0, to = 1.0) val x: Float,
    @FloatRange(from = 0.0, to = 1.0) val y: Float
)

operator fun Position.minus(p: Position): Distance {
    val d = 20
    return Distance((x - p.x) / d, (y - p.y) / d)
}

data class Distance(
    val dx: Float,
    val dy: Float
)

fun value(v: Float): Float {
    return (1f - exp(-v * 5f) * cos(PI / 2f * v)).toFloat()
}

fun InputStream.readText(): String {
    val reader = BufferedReader(reader())
    reader.use {
        return it.readText()
    }
}


@Keep
data class StoredHeart(
    val sx: Float,
    val sy: Float,
    val ex: Float,
    val ey: Float
)

class Heart {
    var distance: Distance = Distance(0f, 0f)
    val id = UUID.randomUUID().toString()
    var alpha by mutableStateOf(0f)
    var startPosition = Position(0f, 0f)
    val currentPosition = mutableStateOf(Position(0f, 0f))
    var show by mutableStateOf(false)

    fun reset() {
        alpha = 0f
        show = false
        startPosition = Position(0f, 0f)
        distance = Distance(0f, 0f)
    }

    fun set(storedHeart: StoredHeart) {
        reset()
        startPosition = Position(storedHeart.sx, storedHeart.sy)
        distance = with(storedHeart) { Distance(ex - sx, ey - sy) }
        show = true
    }


    private val sv
        get() = run {
            val dx = distance.dx
            val dy = distance.dy
            sqrt(dx * dx + dy * dy) / 20
        }

    infix fun map(
        v: Float
    ): Position {
        val sx = startPosition.x
        val sy = startPosition.y
        val dx = distance.dx
        val dy = distance.dy
        return Position(sx + v * dx, sy + v * dy)
    }


    fun drag(t: Float): Float {
        val t0 = 1 / (C * sv)
        return 1 / C * (ln(t + t0) - ln(t0))
    }
}

sealed interface AnimationRequest {
    object Play : AnimationRequest
    object Stop : AnimationRequest
}

class HeartAnimationState {
    val animatable: Animatable<Float, AnimationVector1D> = Animatable(0f)
    private lateinit var models: List<List<StoredHeart>>
    internal var update by mutableStateOf(false)
    internal val placeables = mutableMapOf<Any?, Placeable>()
    internal val items = List(9) { Heart() }
    private var index: Int = 0
    private val _requestUpdate: Channel<AnimationRequest> = Channel()
    private val requestUpdate = _requestUpdate.receiveAsFlow()
    fun play() {
        _requestUpdate.trySend(AnimationRequest.Play)
    }

    private suspend fun animation() = withContext(Dispatchers.Main.immediate) {
        val duration = 1000
        val step = 2f
        val start = 3f
        val end = 30f
        val bound = end - start
        animatable.snapTo(0f)
        animatable.animateTo(
            targetValue = end + items.lastIndex * step,
            animationSpec = tween(duration + items.lastIndex * (step / bound * duration).toInt()) { it }
        ) {
            val v = this.value
            Snapshot.withMutableSnapshot {
                var i = 0
                val iterator = items.iterator()
                while (iterator.hasNext() && index in items.indices) {
                    val heart = iterator.next()
                    if (v in start + i * step..i * step + end) {
                        val t = v - step * i
                        heart.alpha = 1 - (t - start) / bound

                        heart.currentPosition.value = heart map heart.drag(t)
                    } else {
                        heart.alpha = 0f
                    }
                    i++
                }
            }
            if (!isActive) return@animateTo
        }
    }

    @Composable
    internal fun Init() {
        val context = LocalContext.current
        LaunchedEffect(key1 = Unit) {
            loadData(context)
            requestUpdate.collect {
                if (!animatable.isRunning) {
                    animation()
                    setData()
                }
            }
        }
    }

    private fun setData() {
        val model = models[index].shuffled().iterator()
        val items = items.iterator()
        Snapshot.withMutableSnapshot {
            while (model.hasNext()) {
                val item = items.next()
                val modelItem = model.next()
                item.set(modelItem)
            }
            while (items.hasNext()) {
                val item = items.next()
                item.reset()
            }
        }
        index++
        if (index >= 4)
            index = 0
    }

    private suspend fun loadData(context: Context) {
        val paths = (1..4).map { "heart/0$it.anm" }
        withContext(Dispatchers.IO) {
            models = paths.map { path ->
                val model: List<StoredHeart>
                context.assets.open(path).use {
                    val json = it.readText()
                    val type = object : TypeToken<List<StoredHeart>>() {}.type
                    model = Gson().fromJson(json, type)
                }
                model
            }
            setData()
        }
    }
}

@Composable
fun rememberHeartAnimationState() = remember {
    HeartAnimationState()
}

@Composable
internal fun rememberHeartAnimationMeasurePolicy(state: HeartAnimationState) = remember {
    MeasurePolicy { measurables, constraints ->
        val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.map { it.measure(childConstraints) }
        val half = (heartBound / 2).roundToPx()
        val w = constraints.maxWidth
        val h = constraints.maxHeight
        val bound = max(w, h)
        layout(constraints.maxWidth, constraints.maxHeight) {
            Log.i(TAG, "rememberHeartAnimationMeasurePolicy: re layout")
            val iterator = state.items.iterator()
            var i = 0
            while (iterator.hasNext()) {
                val heart = iterator.next()
                placeables.getOrNull(i)?.let { placeable ->
                    if (heart.show) {
                        placeable.placeWithLayer(-half, -half) {
                            this.alpha = heart.alpha
                            val x = (heart.currentPosition.value.x * bound)
                            val y = (heart.currentPosition.value.y * bound)
                            if (x.isNaN() || y.isNaN() || x.isInfinite() || y.isInfinite()) {
                                translationY = 0f
                                translationX = 0f
                            } else {
                                translationX = x
                                translationY = y
                            }
                            this.transformOrigin = TransformOrigin(0.5f, 0.5f)
                            scaleX = 1 - heart.alpha
                            scaleY = 1 - heart.alpha
                        }
                    }
                }
                i++
            }
        }
    }
}

@Composable
fun HeartItem(heart: Heart) {
    val painter =
        rememberVectorPainter(ImageVector.vectorResource(id = R.drawable.ic_heart_filled))
    Icon(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .layoutId(heart.id)
            .size(heartBound),
        tint = Color.White
    )
}

@Composable
fun HeartAnimation(modifier: Modifier, state: HeartAnimationState) {
    state.Init()
    val measurePolicy = rememberHeartAnimationMeasurePolicy(state = state)

    Layout(
        measurePolicy = measurePolicy,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentWidth(align = Alignment.CenterHorizontally)
            .size(bound),
        content = {
            state.items.forEach {
                HeartItem(heart = it)
            }
        }
    )
}
