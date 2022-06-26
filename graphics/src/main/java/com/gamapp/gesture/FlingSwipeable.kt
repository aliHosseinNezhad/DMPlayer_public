package com.gamapp.gesture

import android.util.Log
import androidx.compose.animation.SplineBasedFloatDecayAnimationSpec
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity
import com.gamapp.recyclerlist.stop
import kotlinx.coroutines.*
import kotlin.math.abs
import kotlin.math.min

const val TAG = "FlingSwipeableTAG"

@Composable
fun decay(): DecayAnimationSpec<Float> {
    val density = LocalDensity.current
    val decay = remember {
        SplineBasedFloatDecayAnimationSpec(density).generateDecayAnimationSpec<Float>()
    }
    return decay
}

@Composable
fun rememberFlingSwipeableState(min: Float, max: Float, initialValue: Float): FlingSwipeableState {
    val flingDecay = decay()
    val state = remember(min, max, initialValue) {
        FlingSwipeableState(initialValue = initialValue, min = min, max = max, flingDecay)
    }
    return state
}

class FlingSwipeableState(
    initialValue: Float,
    min: Float,
    max: Float,
    private val flingDecay: DecayAnimationSpec<Float>
) {
    val dragState: DraggableState = com.gamapp.gesture.DraggableState {
        job.stop()
        scrollBy(it)
    }
    val animationSpec: FiniteAnimationSpec<Float> = tween(300)
    internal fun onScroll(it: Float) {
        job.stop()
        scrollBy(it)
    }

    init {
        requirements(min, max, initialValue)
    }


    private fun requirements(min: Float, max: Float, initialValue: Float) {
        require(min < max) {
            "min must be lower than max ." +
                    "min:${min} , max:${max}"
        }
        require(initialValue in listOf(min, max)) {
            "initialValue must be equal to min or max." +
                    "min:${min} , max:${max}"
        }
    }

    private var max by mutableStateOf(max)
    private var min by mutableStateOf(min)
    private var currentValue = mutableStateOf(initialValue)
    val offset: State<Float> = currentValue
    fun internalScrollBy(ds: Float): Float {
        Log.i(TAG, "scrollBy: $ds")
        val expected = currentValue.value + ds
        currentValue.value = expected.coerceIn(min, max)
        val available = expected - currentValue.value
        return ds - available
    }

    fun scrollBy(ds: Float): Float {
        job.stop()
        return internalScrollBy(ds)
    }

    private suspend fun flingInternal(velocity: Float, currentVelocity: Container<Float>): Float =
        coroutineScope {
            return@coroutineScope if (abs(velocity) > 1f) {
                var velocityLeft = velocity
                var lastValue = 0f
                val job = launch {
                    AnimationState(
                        initialValue = 0f,
                        initialVelocity = velocity,
                    ).animateDecay(flingDecay) {
                        val delta = (value - lastValue)
                        Log.i(TAG, "fling: value: $value , delta: $delta")
                        val consumed = internalScrollBy(delta)
                        lastValue = value
                        velocityLeft = this.velocity
                        currentVelocity.value = this.velocity
//                // avoid rounding errors and stop if anything is unconsumed
//                if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
                        if (consumed == 0f) {
                            cancelAnimation()
                            job.stop()
                        }
                    }
                }
                job.join()
                velocity - velocityLeft
            } else {
                currentVelocity.value = velocity
                0f
            }
        }

    internal var job: Job? = null

    data class Container<T>(var value: T)

    suspend fun fling(velocity: Float): Float = coroutineScope {
        if (velocity > 0f && currentValue.value == max) return@coroutineScope 0f
        if (velocity < 0f && currentValue.value == min) return@coroutineScope 0f
        val currentVelocity = Container(velocity)
        Log.i(TAG, "initialVelocity:$velocity")
        job = launch {
            if (velocity != 0f)
                flingInternal(velocity, currentVelocity)
            val df = max - min
            val ds = abs(currentValue.value - min)
            val r = ds / df
            val target = if (r > 0.5f) max else min
            if (currentValue.value !in listOf(min, max))
                launch {
                    Animatable(currentValue.value).animateTo(
                        target,
                        animationSpec = animationSpec
                    ) {
                        currentValue.value = value
                    }
                }
        }
        job?.join()
        velocity - currentVelocity.value
    }
}

fun Modifier.flingSwipeable(
    state: FlingSwipeableState,
    enabled: Boolean = true,
    reverseDirection: Boolean = false,
    orientation: Orientation = Orientation.Vertical
) = draggable(
    orientation = orientation,
    enabled = enabled,
    reverseDirection = reverseDirection,
    onDragStopped = {
        state.fling(it)
    }, onDragStarted = {},
    stateFactory = {
        remember(state) {
            IgnorePointerDraggableState(state.dragState)
        }
    })