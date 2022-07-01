package com.gamapp.layout.categorypager

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.IntSize
import com.gamapp.gesture.DraggableState
import com.gamapp.gesture.IgnorePointerDraggableState
import com.gamapp.gesture.draggable
import com.gamapp.recyclerlist.stop
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs

class Runner {
    var first = true
    inline fun run(block: () -> Unit) {
        if (first) {
            block()
            first = false
        }
    }
}

sealed class Progress {
    class Fixed(val index: Int? = null, val requesterId: Any? = null) : Progress() {
        override fun toString(): String {
            return "Empty"
        }
    }

    class Data(
        val from: Int,
        val to: Int,
        val value: Float,
        val totolValue: Float,
        val requesterId: Any? = null
    ) :
        Progress() {
        override fun toString(): String {
            return "Progress(from:$from , to:$to , value:$value , requesterId:$requesterId)"
        }
    }
}

class Index(val index: Int, val requesterId: Any?) {
    override fun toString(): String {
        val id = if (requesterId == null) "null" else (requesterId::class.java).simpleName
        return "Index(index:$index ,requesterId:$id)"
    }
}

fun progress(from: Int, to: Int, value: Float, totalValue: Float, requesterId: Any?): Progress {
    return Progress.Data(from, to, value, totalValue, requesterId)
}

@Composable
fun rememberCategoryPagerState(initialIndex: Int): CategoryPagerState {
    val state = rememberSaveable(saver = CategoryPagerState.Saver) {
        CategoryPagerState(Index(initialIndex, null))
    }
    return state
}

class CategoryPagerState(initialIndex: Index) {
    companion object {
        val Saver: Saver<CategoryPagerState, *> = Saver(save = {
            it.currentIndex
        }, restore = {
            CategoryPagerState(Index(it, null))
        })
    }

    val dragState = DraggableState {
        job.stop()
        scrollByInternal(it, requestId = null, targetIndex = null)
    }
    internal val items = mutableMapOf<Any?, IntSize>()
    internal var size = IntSize.Zero
    internal var min = 0f
    internal var max = 0f
    private val defaultAnimationSpec = tween<Float>(350)
    internal var scroll by mutableStateOf(0f)
    private val _progress = mutableStateOf<Progress>(Progress.Fixed())
    val progress: State<Progress> = _progress
    private val _currentIndexState = mutableStateOf(initialIndex)
    val currentIndexState: State<Index> = _currentIndexState
    val currentIndex get() = currentIndexState.value.index

    @PublishedApi
    internal var internalEnabled by mutableStateOf(true)

    @PublishedApi
    internal var enabled by mutableStateOf(true)
    internal val runner = Runner()

    fun setEnable(enable: Boolean) {
        enabled = enable
    }

    @PublishedApi
    internal fun setIndex(index: Index) {
        if (currentIndex != index.index)
            _currentIndexState.value = index
    }

    @PublishedApi
    internal fun scrollByInternal(
        ds: Float,
        targetIndex: Int?,
        requestId: Any?
    ): Float {
        if (checkIndex(currentIndex)) return 0f
        val expected = scroll + ds
        scroll = expected.coerceIn(min, max)
        val available = expected - scroll
        val consumed = ds - available
        if (abs(consumed) > 0f) {
            val offset = currentIndex * size.width
            val current = scroll
            val dif = (current - offset)
            val target = targetIndex ?: (currentIndex + if (dif > 0f) 1 else -1).coerceIn(
                items.values.indices
            )
            val count = abs(target - currentIndex).coerceAtLeast(1)
            val value = dif / (size.width * count)
            val percent = ((scroll - min) / (max - min)).coerceIn(0f, 1f)
            _progress.value = progress(
                from = currentIndex,
                to = target,
                value = value,
                requesterId = requestId,
                totalValue = percent
            )
        }
        return consumed
    }

    private fun checkIndex(index: Int) =
        (items.isEmpty() || index !in items.values.indices || currentIndex !in items.values.indices)


    internal fun scrollTo(s: Float) {
        if (checkIndex(currentIndex)) return
        scroll = s.coerceIn(min, max)
    }

    suspend fun animateTo(
        index: Int,
        requesterId: Any,
        animationSpec: FiniteAnimationSpec<Float>
    ) {
        if (checkIndex(index)) return
        val target = index * size.width
        coroutineScope {
            job = launch {
                goto(
                    targetOffset = target,
                    index = index,
                    requesterId = requesterId,
                    animationSpec = animationSpec
                )
            }
        }
    }

    internal fun snapTo(index: Int, requesterId: Any?) {
        if (checkIndex(index)) return
        val target = index * size.width
        scrollByInternal(target - scroll, targetIndex = index, requestId = null)
        _currentIndexState.value = (Index(index, requesterId))
    }

    private suspend fun goto(
        targetOffset: Int,
        animationSpec: FiniteAnimationSpec<Float>,
        index: Int,
        requesterId: Any? = null
    ) {
        coroutineScope {
            var pv = scroll
            if (targetOffset.toFloat() != scroll)
                Animatable(scroll).animateTo(
                    targetValue = targetOffset.toFloat(),
                    animationSpec = animationSpec
                ) {
                    if (!isActive) {
                        return@animateTo
                    }
                    val delta = value - pv
                    scrollByInternal(delta, targetIndex = index, requestId = requesterId)
                    pv = value
                }
            _progress.value = Progress.Fixed(index = index, requesterId = requesterId)
            _currentIndexState.value = (Index(index, requesterId = requesterId))
        }
    }

    internal var job: Job? = null

    @PublishedApi
    internal suspend fun fling(velocity: Float) = coroutineScope {
        job = launch {
            val s = scroll.toInt()
            val l = (s % size.width) / size.width.toFloat()
            val i = s / size.width
            if (abs(velocity) > 200) {
                val sign = if (velocity > 0) 1 else 0
                val index = (i + sign).coerceIn(items.values.indices)
                val target = index * size.width
                goto(
                    targetOffset = target,
                    index = index,
                    requesterId = null,
                    animationSpec = defaultAnimationSpec
                )
            } else {
                if (l > 0.5f) goto(
                    targetOffset = (i + 1) * size.width,
                    index = i + 1,
                    requesterId = null,
                    animationSpec = defaultAnimationSpec
                )
                else goto(
                    i * size.width,
                    index = i,
                    requesterId = null,
                    animationSpec = defaultAnimationSpec
                )
            }
        }
    }

    fun checkWithProgress(it: Progress) {
        internalEnabled = it is Progress.Fixed
    }


}


@PublishedApi
@Composable
internal fun rememberCategoryPagerMeasurePolicy(state: CategoryPagerState) = remember(state) {
    MeasurePolicy { measurables, constraints ->
        val w = constraints.maxWidth
        val h = constraints.maxHeight
        val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        state.items.clear()
        val placeables = measurables.mapIndexed { index, measurable ->
            val placeable = measurable.measure(childConstraints)
            state.items[index] = IntSize(placeable.width, placeable.height)
            placeable
        }
        initState(items = placeables, state = state, width = w)
        state.size = IntSize(w, h)
        layout(w, h) {
            if (state.currentIndex !in measurables.indices) {
                state.setIndex(Index(0, requesterId = null))
            }
            var s = 0
            placeables.forEach { placeable ->
                placeable.placeWithLayer(s - state.scroll.toInt(), 0)
                s += placeable.width
            }
        }
    }
}

fun initState(items: List<Placeable>, state: CategoryPagerState, width: Int) {
    if (items.isEmpty()) return
    state.min = 0f
    state.max = (items.sumOf { it.width } - items.last().width).toFloat()
    state.runner.run {
        state.scrollTo(state.currentIndex * width.toFloat())
    }
}

@Composable
fun CategoryPager(
    modifier: Modifier,
    state: CategoryPagerState,
    content: @Composable () -> Unit
) {
    val measurePolicy = rememberCategoryPagerMeasurePolicy(state = state)
    Layout(
        measurePolicy = measurePolicy, modifier = Modifier
            .then(modifier)
            .draggable(
                orientation = Orientation.Horizontal,
                reverseDirection = true,
                onDragStopped = {
                    launch {
                        state.fling(velocity = it)
                    }
                },
                enabled = state.internalEnabled && state.enabled,
                stateFactory = {
                    remember(state) {
                        IgnorePointerDraggableState(state.dragState)
                    }
                },
            ),
        content = content
    )
}