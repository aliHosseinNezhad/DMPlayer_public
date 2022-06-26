package com.gamapp.layout

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.gamapp.gesture.IgnorePointerDraggableState
import com.gamapp.gesture.decay
import com.gamapp.gesture.draggable
import com.gamapp.recyclerlist.stop
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs

const val TAG = "PopupLayoutTAG!@"

class Padding(
    val betweenItems: Dp = 0.dp,
    val top: Dp = 0.dp,
    val bottom: Dp = 0.dp
)

class PopupLayoutState(private val flingDecay: DecayAnimationSpec<Float>) {
    val dragState: DraggableState = DraggableState {
        job.stop()
        scrollBy(it)
    }

    @PublishedApi
    internal var min = 0f

    @PublishedApi
    internal var max = 0f

    @PublishedApi
    internal var scroll by mutableStateOf(0f)

    @PublishedApi
    internal fun scrollBy(ds: Float): Float {
        val expected = scroll + ds
        scroll = expected.coerceIn(min, max)
        val available = expected - scroll
        val consumed = ds - available
        return consumed
    }

    @PublishedApi
    internal var job: Job? = null
    suspend fun fling(velocity: Float) {
        coroutineScope {
            job = launch {
                var velocityLeft = velocity
                var lastValue = 0f
                AnimationState(
                    initialValue = 0f,
                    initialVelocity = velocity,
                ).animateDecay(flingDecay) {
                    val delta = (value - lastValue)
                    val consumed = scrollBy(delta)
                    lastValue = value
                    velocityLeft = this.velocity
                    if (abs(delta - consumed) > 0.5f || !isActive) {
                        this.cancelAnimation()
                        return@animateDecay
                    }
                }
            }
        }
    }
}

@PublishedApi
@Composable
internal fun rememberPopupLayoutMeasurePolicy(state: PopupLayoutState, padding: Padding) =
    remember(state, padding) {
        MeasurePolicy { measurables, constraints ->
            val maxWidth = (measurables.maxOfOrNull { it.maxIntrinsicWidth(0) } ?: 0)
                .coerceIn(constraints.minWidth, constraints.maxWidth)
            val childConstraints =
                Constraints.fixedWidth(maxWidth)
                    .copy(minHeight = 0, maxHeight = constraints.maxHeight)
            val placeables = Array<Placeable?>(measurables.size) { null }
            measurables.fastForEachIndexed { index, measurable ->
                placeables[index] = measurable.measure(childConstraints)
            }
            val maxHeight =
                placeables.requireNoNulls().sumOf { it.height }
                    .coerceIn(constraints.minHeight, constraints.maxHeight)
            val sb = padding.betweenItems.roundToPx()
            val top = padding.top.roundToPx()
            state.min = 0f
            state.max = (placeables.requireNoNulls()).let { it ->
                it.sumOf { it.height } + top + it.lastIndex * sb - maxHeight
            }.coerceAtLeast(0) + padding.bottom.toPx()
            layout(
                maxWidth,
                maxHeight
            ) {
                var h = top
                placeables.forEach {
                    it?.let { placeable ->
                        placeable.place(0, h - state.scroll.toInt())
                        h += placeable.height + sb
                    }
                }
            }
        }
    }

@Composable
fun PopupLayout(
    modifier: Modifier,
    padding: Padding = Padding(),
    content: @Composable () -> Unit
) {
    val flingDecay = decay()
    val state = remember {
        PopupLayoutState(flingDecay)
    }
    Layout(
        measurePolicy = rememberPopupLayoutMeasurePolicy(state = state, padding = padding),
        modifier = modifier.draggable(
            orientation = Orientation.Vertical,
            reverseDirection = true,
            onDragStopped = { velocity ->
                state.fling(velocity)
            },
            stateFactory = {
                remember(state) {
                    IgnorePointerDraggableState(state.dragState)
                }
            }
        ),
        content = content
    )
}