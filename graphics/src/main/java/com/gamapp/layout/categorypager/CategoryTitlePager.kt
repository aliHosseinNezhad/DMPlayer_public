package com.gamapp.layout.categorypager

import android.util.Log
import androidx.compose.animation.SplineBasedFloatDecayAnimationSpec
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import com.gamapp.gesture.IgnorePointerDraggableState
import com.gamapp.gesture.draggable
import com.gamapp.recyclerlist.stop
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

const val TAG = "categoryChangetag"

@Composable
fun rememberCategoryTitlePagerState(initialIndex: Int): CategoryTitlePagerState {
    val state =
        rememberSaveable(saver = CategoryTitlePagerState.Saver) {
            CategoryTitlePagerState(Index(initialIndex, null))
        }
    return state
}

class CategoryTitlePagerState(
    initialIndex: Index,
) {
    companion object {
        val Saver: Saver<CategoryTitlePagerState, *> = Saver(save = {
            it.currentIndex
        }, restore = {
            CategoryTitlePagerState(Index(it, null))
        })
    }

    @PublishedApi
    internal var internalEnabled by mutableStateOf(true)

    @PublishedApi
    internal var enabled by mutableStateOf(true)

    fun setEnable(enable: Boolean) {
        enabled = enable
    }

    internal var density: Density? = null
    private val flingDecay: DecayAnimationSpec<Float>?
        get() = run {
            density?.let {
                SplineBasedFloatDecayAnimationSpec(it)
                    .generateDecayAnimationSpec()
            }
        }

    @PublishedApi
    internal val draggableState: DraggableState = DraggableState {
        job.stop()
        scrollBy(it, null, null)
    }
    internal var size: IntSize = IntSize.Zero
    internal var max: Float = 0f
    internal var min: Float = 0f
    var scroll by mutableStateOf(0f)
        internal set
    internal val items = mutableMapOf<Any?, IntSize>()
    private val _currentIndexState = mutableStateOf(initialIndex)
    val currentIndexState: State<Index> = _currentIndexState
    private val _progress = mutableStateOf<Progress>(Progress.Empty)
    val progress: State<Progress> = _progress


    internal fun setIndex(index: Index) {
        _currentIndexState.value = index
    }

    val currentIndex get() = currentIndexState.value.index
    internal val runner = Runner()

    @PublishedApi
    internal fun scrollBy(ds: Float, targetIndex: Int?, requesterId: Any?): Float {
        if (checkIndex(currentIndex)) return 0f
        val expected = scroll + ds
        scroll = expected.coerceIn(min, max)
        val available = expected - scroll
        val consumed = ds - available
        if (abs(consumed) > 0f) {
            val offset = positionOfIndex(currentIndex)
            val current = scroll
            val dif = current - offset
            val target = targetIndex ?: (currentIndex + if (dif > 0f) 1 else -1).coerceIn(
                items.values.indices
            )
            val targetOffset = positionOfIndex(target)
            val bound = abs(targetOffset - offset).coerceAtLeast(1)
            val value = dif / (bound)
            _progress.value = progress(
                from = currentIndex,
                to = target,
                value = value,
                requesterId = requesterId
            )
        }
        return consumed
    }

    private fun scrollTo(s: Float) {
        if (checkIndex(currentIndex)) return
        scroll = s.coerceIn(min, max)
    }

    @PublishedApi
    internal var job: Job? = null

    internal fun positionOfIndex(index: Int): Int {
        var w = 0
        val items = items.values.toList()
        for (i in 0..index) {
            w += items[i].width
        }
        return w - items.first().width / 2 - items[index].width / 2
    }

    private fun Progress.check(startKey: List<Any?>): Boolean {
        if (this == Progress.Empty) return false
        this as Progress.Data
        if (this.requesterId == this@CategoryTitlePagerState) return false
        if (this.requesterId in startKey) return false
        val value = this.value
        if (
            value == Float.NEGATIVE_INFINITY ||
            value == Float.POSITIVE_INFINITY ||
            value.isNaN()
        ) return false
        return true
    }

    private val progressTemp = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()
    fun moveWithProgress(progress: Progress, ignoredKeys: List<Any?> = listOf()) {
        internalEnabled = progress is Progress.Empty
        if (progress.check(ignoredKeys)) {
            progress as Progress.Data
            val from = progress.from
            val to = progress.to
            val value = abs(progress.value)
            if (from == to) {
                val to2 = progressTemp.keys.firstOrNull {
                    it.first == from
                } ?: return
                val bounds = progressTemp[to2] ?: return
                val df = bounds.second - bounds.first
                val v = value * df
                scrollTo(bounds.first + v)
            } else {
                val bounds = progressTemp.getOrPut(from to to) {
                    val s = positionOfIndex(from)
                    val e = positionOfIndex(to)
                    s to e
                }
                val df = bounds.second - bounds.first
                val v = value * df
                scrollTo(bounds.first + v)
            }
        } else {
            progressTemp.clear()
        }
    }

    private fun checkIndex(index: Int) =
        (items.isEmpty() || index !in items.values.indices || currentIndex !in items.values.indices)

    suspend fun animateTo(index: Int, requesterId: Any) = coroutineScope {
        if (checkIndex(index)) return@coroutineScope
        job = launch {
            if (items.isEmpty() || !items.keys.contains(index)) return@launch
            val target = positionOfIndex(index)
            goto(targetOffset = target, index = index, requesterId = requesterId)
            job?.join()
        }
        job?.join()
    }

    fun snapTo(index: Int, requesterId: Any?) {
        if (checkIndex(index)) return
        val s = positionOfIndex(index)
        scrollTo(s.toFloat())
        _currentIndexState.value = Index(index = index, requesterId = requesterId)
    }

    private suspend fun flingInternal(velocity: Float): Float {
        var velocityLeft = velocity
        var lastValue = 0f
        AnimationState(
            initialValue = 0f,
            initialVelocity = velocity,
        ).animateDecay(flingDecay!!) {
            val delta = (value - lastValue)
            val consumed = scrollBy(delta, targetIndex = null, requesterId = null)
            lastValue = value
            velocityLeft = this.velocity
            if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
        }
        return velocity - velocityLeft
    }

    @PublishedApi
    internal suspend fun fling(velocity: Float) = coroutineScope {
        if (checkIndex(currentIndex)) return@coroutineScope 0f
        job = launch {
            flingInternal(velocity)
            val item = findCurrentIndex()
            val index = item.first
            val target = item.second
            goto(targetOffset = target, index = index, requesterId = null)
        }
        job?.join()
    }

    private suspend fun goto(targetOffset: Int, index: Int, requesterId: Any?) {
        coroutineScope {
            _currentIndexState.value = Index(index, requesterId)
            var pv = scroll.toFloat()
            if (scroll != targetOffset.toFloat())
                Animatable(scroll).animateTo(
                    targetValue = targetOffset.toFloat(),
                    animationSpec = tween(400)
                ) {
//                    scroll = value
                    val delta = value - pv
                    scrollBy(delta, targetIndex = index, requesterId = requesterId)
                    pv = value
                }
            _progress.value = Progress.Empty
        }

    }

    private fun findCurrentIndex(): Pair<Int, Int> {
        val items = items.values.toList()
        val w0 = items.first().width
        var s = 0
        var e = 0
        for (i in items.indices) {
            s = e
            e += items[i].width
            if (scroll.toInt() + w0 / 2 in s..e) return i to (e - w0 / 2 - items[i].width / 2)
        }
        return 0 to w0 / 2
    }

    @PublishedApi
    internal suspend fun onTap(offset: Offset, size: IntSize) {
        val items = items.values.toList()
        if (items.isEmpty()) return
        val x = offset.x
        val w = size.width
        val end =
            items.sumOf { it.width }.toFloat() - items.last().width / 2 - items.first().width / 2
        val start = w / 2 - items.first().width / 2
        var off = 0
        items.forEachIndexed { index, item ->
            val s = start - scroll + off
            off += item.width
            val e = start - scroll + off
            if (x in s..e) {
                val target = off - items.first().width / 2 - item.width / 2
                goto(targetOffset = target, index = index, requesterId = null)
                return
            }
        }
    }
}

@Composable
fun rememberCategoryPagerMeasurePolicy(state: CategoryTitlePagerState) = remember(state) {
    MeasurePolicy { measurables, constraints ->
        state.size = IntSize(constraints.maxWidth, constraints.maxHeight)
        if (measurables.isEmpty()) return@MeasurePolicy layout(
            constraints.maxWidth,
            constraints.maxHeight
        ) {}
        val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        state.density = this
        state.items.clear()
        val placeables = measurables.mapIndexed { i, measurable ->
            val placeable = measurable.measure(childConstraints)
            state.items[i] = IntSize(placeable.width, placeable.height)
            placeable
        }
        val w = constraints.maxWidth
        val childWidth = placeables.first().width
        state.max =
            placeables.sumOf { it.width }.toFloat() - placeables.last().width / 2 - childWidth / 2
        state.min = 0f
        val start = w / 2 - childWidth / 2
        state.runner.run {
            state.scroll = state.positionOfIndex(state.currentIndex).toFloat()
        }
        layout(constraints.maxWidth, constraints.maxHeight) {
            if (state.currentIndex !in measurables.indices) {
                state.setIndex(Index(0, requesterId = null))
            }
            var offset = 0
            placeables.forEach { placeable ->
                place(
                    placeable,
                    start,
                    state,
                    offset = {
                        offset += it
                        offset
                    },
                    width = w
                )
            }
        }
    }
}

fun Float.safe(): Float {
    return if (this.isInfinite() || this.isNaN()) 0f else this
}

fun Placeable.PlacementScope.place(
    placeable: Placeable,
    start: Int,
    state: CategoryTitlePagerState,
    offset: (value: Int) -> Int,
    width: Int
) {
    val cw = placeable.width
    val s = start - state.scroll.toInt() + offset(0)
    val e = start - state.scroll.toInt() + offset(placeable.width)
    val v = ((abs((e + s) / 2 - width / 2).coerceIn(0..cw).toFloat() / cw))
    placeable.placeWithLayer(s, 0) {
        alpha = 1 - v * 0.4f
        val scale = 1 - v * 0.45f
        scaleX = scale
        scaleY = scale
    }
}

@Composable
fun <T> CategoryTitlePager(
    items: List<T>,
    content: @Composable (T) -> Unit,
    state: CategoryTitlePagerState,
    modifier: Modifier
) {
    val measurePolicy = rememberCategoryPagerMeasurePolicy(state)
    val scope = rememberCoroutineScope()
    val currentState = rememberUpdatedState(newValue = state)
    Layout(
        measurePolicy = measurePolicy,
        modifier = Modifier.then(modifier),
        content = {
            items.forEach {
                content(it)
            }
        }
    )
    Box(modifier = Modifier
        .then(modifier)
        .draggable(
            orientation = Orientation.Horizontal,
            onDragStopped = {
                launch {
                    state.fling(velocity = it)
                }
            },
            stateFactory = {
                remember(state) {
                    IgnorePointerDraggableState(state.draggableState)
                }
            },
            reverseDirection = true,
            enabled = state.enabled && state.internalEnabled
        )
        .pointerInput(
            key1 = state,
            key2 = remember {
                derivedStateOf {
                    currentState.value.enabled && currentState.value.internalEnabled
                }
            }.value
        ) {
            if (!state.enabled) return@pointerInput
            detectTapGestures {
                scope.launch {
                    state.onTap(it, size)
                }
            }
        })
}