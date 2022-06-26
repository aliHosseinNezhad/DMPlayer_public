package com.gamapp.pager

import android.util.Log
import androidx.compose.animation.SplineBasedFloatDecayAnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.generateDecayAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.gamapp.layout.LayoutWithConstraints
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random

const val TAG = "DragGestureDetect"
const val TAG2 = "DragGestureDetect2"
const val TAG3 = "DragGestureDetect3"
const val TAG4 = "DragGestureDetect4"


@Composable
fun Pager() {
    val baseColors = remember {
        listOf(
            Color.Blue,
            Color.Cyan,
            Color.Magenta,
            Color(0, 100, 255),
            Color.Yellow,
            Color.Red,
            Color.Green,
        )
    }
    val colors = remember {
        MutableStateFlow(baseColors)
    }
    val shuffle = remember {
        MutableStateFlow(false)
    }
    val data = remember {
        MutableStateFlow(baseColors)
    }
    val selectedItem = remember {
        MutableStateFlow(baseColors[3])
    }
    LaunchedEffect(key1 = null) {
        colors.combine(shuffle, transform = { a, b ->
            if (b)
                a.shuffled()
            else a
        }).collect {
            data.emit(it)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
//        RecyclerHorizontalPager(
//            state = state,
//            modifier = Modifier
//                .border(5.dp, color = Color.Black),
//            data = data.collectAsState().value,
//            content = {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(it)
//                ) {
//                    Text(
//                        text = baseColors.indexOf(it).toString(),
//                        modifier = Modifier.align(Alignment.Center)
//                    )
//                }
//            },
//            infinity = false
//        )
        val isShuffle by shuffle.collectAsState()
        val scope = rememberCoroutineScope()
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
        ) {
            Button(
                onClick = {
                    scope.launch {
                        shuffle.emit(!shuffle.value)
                    }
                }
            ) {
                Text(text = if (isShuffle) "is Shuffle" else "not Shuffle")
            }
            Button(onClick = {
                val index = Random(System.currentTimeMillis()).nextInt(0, data.value.size)
                selectedItem.value = data.value[index]
            }) {
                Text(
                    text = selectedItem.collectAsState().value.let {
                        baseColors.indexOf(it).toString()
                    },
                )
            }
        }

    }
}

fun main() {
    val list = (0..29).map { it }
    val shuffled = list.shuffled()
    val index = Random(System.currentTimeMillis()).nextInt(0, 29)
    val i = shuffled.indexOf(list[index])
    val di = index - i
    println(di)
    println("i:$i")
    println("index:$index")
    println(list)
    println(shuffled)
    val new = if (di > 0) {
        val sub = shuffled.subList(shuffled.lastIndex - di + 1, shuffled.size)
        val next = shuffled.subList(0, shuffled.lastIndex - di + 1)
        sub + next
    } else {
        val sub = shuffled.subList(0, -di)
        val next = shuffled.subList(-di, shuffled.size)
        next + sub
    }
    println(new)

}

fun <T> List<T>.item(index: Int): T? {
    return when {
        size >= 3 -> {
            val i = indexOf(index, size)
            this[i]
        }
        size == 2 -> {
            when (index) {
                -1 -> null
                0 -> this[index]
                1 -> this[index]
                else -> null
            }
        }
        size == 1 -> {
            if (index == 0) this[index]
            else null
        }
        else -> null
    }
}

fun <T> List<T>.convertIndex(index: Int): Int {
    val i = index % size
    return if (i < 0) {
        size + i
    } else {
        i
    }
}

fun getIndices(w: Int, s: Int): List<Int> {
    val m = -s
    val t = m / w.toFloat()
    val z1 = t + 2.5f
    val k1 = (z1 / 3f).dec.toInt()
    val z2 = t + 1.5f
    val k2 = (z2 / 3f).dec.toInt()
    val z3 = t + 0.5f
    val k3 = (z3 / 3f).dec.toInt()
    val i1 = 3 * k1 - 1
    val i2 = 3 * k2
    val i3 = 3 * k3 + 1
    return listOf(i1, i2, i3)
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

@Composable
inline fun <T> RecyclerHorizontalPager(
    state: RecyclerPagerState<T>,
    modifier: Modifier,
    content: @Composable (T) -> Unit,
) {
    LayoutWithConstraints(modifier = modifier, content = {
        LaunchedEffect(key1 = state) {
            state.width = constraints.value.maxWidth
            state.height = constraints.value.maxHeight
            state.min.value = 0f
            state.max.value = constraints.value.maxWidth *
                    state.data.value.lastIndex.toFloat()
        }
        RecyclerHorizontalPagerImpl(
            state = state,
            content = content
        )
    })
}

val Float.dec: Float
    get() = run {
        if (this < 0f) this - 1f
        else this
    }

@Composable
fun <T> rememberRecyclerPagerState(
    initItem: T,
    data: List<T>,
    infinity: Boolean = false
): RecyclerPagerState<T> {
    val density = LocalDensity.current
    val state = remember {
        RecyclerPagerState<T>(
            flingDecay = SplineBasedFloatDecayAnimationSpec(density)
                .generateDecayAnimationSpec(),
            _currentPageItem = MutableStateFlow(initItem to false)
        )
    }
    val isInfinity = if (state.data.collectAsState().value.size < 3) false else infinity
    LaunchedEffect(key1 = isInfinity) {
        state.infinity.value = isInfinity
    }
    LaunchedEffect(key1 = data) {
        state.data.emit(data.toList())
    }
    return state
}

class RecyclerPagerState<T>(
    private val flingDecay: DecayAnimationSpec<Float>,
    internal val _currentPageItem: MutableStateFlow<Pair<T, Boolean>>
) {
    private val middleIndex: Int
        get() = run {
            val indices = getIndices(width, scroll.value.toInt())
            indices.mapIndexed { index, it ->
                currentData[index] to it
            }.sortedBy {
                it.second
            }[1]
            indices.sortedBy { it }[1] + di.value
        }
    private val middleItem: T?
        get() = run {
            val indices = getIndices(width, scroll.value.toInt())
            indices.mapIndexed { index, it ->
                currentData[index] to it
            }.sortedBy {
                it.second
            }[1].first
        }

    @PublishedApi
    internal val data: MutableStateFlow<List<T>> = MutableStateFlow(listOf())

    /**
     * [currentPageIndex] represent index of current middle page in pager.
     * if that change by user ,this boolean value will be true
     * and else if it change without user touch will be false
     */
    val currentPageIndex: StateFlow<Pair<T, Boolean>> = _currentPageItem

    @PublishedApi
    internal val update: MutableState<Boolean> = mutableStateOf(false)

    @PublishedApi
    internal var width: Int = 0

    @PublishedApi
    internal var height: Int = 0

    @PublishedApi
    internal val min: MutableState<Float> = mutableStateOf(0f)

    @PublishedApi
    internal val max: MutableState<Float> = mutableStateOf(0f)

    @PublishedApi
    internal var flingJob: Job? = null

    @PublishedApi
    internal var previousItem: T? = null
    private var previousIndex: Int = 0

    @PublishedApi
    internal val dispatcher = NestedScrollDispatcher()
    private val _scroll: MutableState<Float> = mutableStateOf(0f)
    val scroll: State<Float> = _scroll

    @PublishedApi
    internal val di: MutableState<Int> = mutableStateOf(0)

    @PublishedApi
    internal val currentData: SnapshotStateList<T?> = mutableStateListOf(
        null,
        null,
        null
    )
    internal val infinity: MutableState<Boolean> = mutableStateOf(false)

    @PublishedApi
    internal val postSkipToPage: MutableSharedFlow<T> =
        MutableSharedFlow(replay = 1, extraBufferCapacity = 1)

    suspend fun skipToPage(item: T) {
        postSkipToPage.emit(item)
    }


    private fun scrollBy(value: Float) {
        _scroll.value = if (infinity.value) value else value.coerceIn(
            -max.value, -min.value
        )
        Log.i(TAG, "s: ${_scroll.value} , min:${min.value} , max:${max.value} \n width:$width")
    }

    private fun reset(lastIndex: Int) {
        min.value = 0f
        max.value = (width * lastIndex).toFloat()
        di.value = 0
        Log.i(TAG4, "reset: previousSet:$previousItem")
        previousItem = null
        _scroll.value = 0f
    }

    @PublishedApi
    internal suspend fun fling(available: Velocity): Velocity {
        Log.i(TAG3, "fling: started *****")
        Log.i(TAG3, "fling scroll: ${scroll.value}")
        Log.i(TAG3, "fling: min:${min.value}")
        Log.i(TAG3, "fling: previous: $previousItem")
        if (abs(available.x) > 800f) {
            onEndFling(available)
        } else
            onEnd()
        refresh()
        Log.i(TAG3, "fling: previous: $previousItem")
        Log.i(TAG3, "fling: min:${min.value}")
        Log.i(TAG3, "fling: scroll: ${scroll.value}")
        val item = previousItem ?: return Velocity.Zero
        _currentPageItem.emit(item to true)
        return Velocity.Zero
    }

    private suspend fun onEnd() {
        val s = -_scroll.value
        val ds = (s - min.value) % width
        val change = if (abs(ds / width) > 0.5f) {
            width - ds
        } else {
            -ds
        }
        animate(
            initialValue = s,
            targetValue = s + change,
            animationSpec = tween(200)
        ) { v, _ ->
            scrollBy(-v)
        }
    }

    private suspend fun onEndFling(velocity: Velocity) {
        val s = -_scroll.value
        val ds = (s - min.value) % width
        val initialValue: Float = s
        val targetValue: Float = if (velocity.x > 0f) {
            s - ds
        } else {
            s + width - ds
        }
        if (targetValue > max.value) {
            scrollBy(-s)
            return
        }
        animate(
            initialValue = initialValue,
            targetValue = targetValue,
            initialVelocity = -velocity.x,
            animationSpec = tween(200)
        ) { v, _ ->
            scrollBy(-v)
        }
    }

    @PublishedApi
    internal fun scroll(available: Offset): Offset {
        scrollBy(available.x + _scroll.value)
        return Offset.Zero
    }

    fun calculateBounds() {
        val item = middleItem
        if (item == null || !data.value.contains(previousItem)) {
            reset(data.value.lastIndex)
            refresh()
        } else {
            val i = middleIndex
            val index = data.value.indexOf(item)
            Log.i(TAG4, "previousItem: $item")
            Log.i(TAG4, "middleIndex: $middleIndex")
            Log.i(TAG4, "calculateBounds: index:$index")
            Log.i(TAG4, "min1: ${min.value}")
            di.value += index - i
            val m = -scroll.value
            val ds = m - min.value
            val s = (ds.toInt() / width) * width + min.value
            min.value = s - width * index
            max.value = s + (data.value.lastIndex - index) * width
            Log.i(TAG4, "min2: ${min.value}")
            refresh()
        }
    }

    private fun updatePreviousItem(indices: List<Int> = getIndices(width, scroll.value.toInt())) {
        indices.sortedBy { it }[1].let {
            val index = it + di.value
            previousItem = data.value.item(index)
            previousIndex = data.value.indexOf(previousItem)
        }
    }

    fun refresh() {
        val stacktrace = Thread.currentThread().stackTrace
        val stacks = stacktrace.map { it.methodName }
        val w = width
        val s = scroll.value.toInt()
        val indices = getIndices(w, s)
//        Log.i(TAG4, "refresh: ${stacks.filterIndexed { index, _ -> index > 2 }}")
//        Log.i(TAG4, " refresh: previousSet:$previousItem")
        updatePreviousItem(indices)
//        Log.i(TAG4, " refresh: previousSet:$previousItem")
        indices.forEachIndexed { index, it ->
            currentData[index] = data.value.item(it + di.value)
        }
    }

    @PublishedApi
    internal fun readySkipToPage(item: T) {
        val index = data.value.indexOf(item)
//        Log.i(TAG2, "index: $index ")
        if (index == -1) return
//        Log.i(TAG2, "min: ${min.value}")
        val s = min.value + width * index
        Log.i(TAG2, "skipToPage: $previousItem")
        scrollBy(-s)
        refresh()
        Log.i(TAG2, "skipToPage: $previousItem")
    }
}

@Composable
@PublishedApi
internal inline fun <T> RecyclerHorizontalPagerImpl(
    state: RecyclerPagerState<T>,
    content: @Composable (T) -> Unit,
) {
    val scrollable = Modifier
        .fillMaxSize()
        .pointerInput(null, block = {
            coroutineScope {
                val velocityTracker = VelocityTracker()
                detectHorizontalDragGestures(onDragEnd = {
                    val velocity = velocityTracker.calculateVelocity()
                    velocityTracker.resetTracking()
                    state.flingJob = state.dispatcher.coroutineScope.launch {
                        val consumed = state.dispatcher.dispatchPreFling(velocity)
                        val available = velocity - consumed
                        val consumed2 = state.fling(available = available)
                        val overFlowSpeed =
                            state.dispatcher.dispatchPostFling(consumed2, available - consumed2)
                    }
                }) { change, dragAmount ->
                    velocityTracker.addPointerInputChange(change)
                    val available = Offset(dragAmount, 0f)
                    val consumed = state.dispatcher.dispatchPreScroll(
                        available = available,
                        NestedScrollSource.Drag
                    )
                    val available2 = available - consumed
                    val consumed2 = state.scroll(available2)
                    val consumed3 = state.dispatcher.dispatchPostScroll(
                        consumed = consumed2,
                        available = available2 - consumed2,
                        NestedScrollSource.Drag
                    )
                }
            }
        })
        .nestedScroll(
            connection = object : NestedScrollConnection {},
            dispatcher = state.dispatcher
        )
    LaunchedEffect(key1 = Unit) {
        launch {
            state.postSkipToPage
                .combine(state.data, transform = { a, _ -> a })
                .collect {
                    Log.i(TAG4, "track: $it data: ${state.data.value}")
                    state.calculateBounds()
                    state.refresh()
                    state.readySkipToPage(it)
                }
        }
        launch {
            snapshotFlow { state.scroll.value }.collect {
                state.refresh()
            }
        }
    }
    Layout(
        modifier = Modifier
            .fillMaxSize()
            .then(scrollable),
        content = {
            state.currentData.forEach { item ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    if (item != null)
                        content(item)
                }
            }
        },
        measurePolicy = rememberRecyclerPagerMeasurePolicy(state = state)
    )
}


@Composable
fun <T> rememberRecyclerPagerMeasurePolicy(state: RecyclerPagerState<T>) = remember(state) {
    MeasurePolicy { measurables, constraints ->
        val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.map { it.measure(childConstraints) }
        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.let {
                val p1 = it[0]
                val p2 = it[1]
                val p3 = it[2]
                val w = p1.width
                val s = state.scroll.value.toInt()
                val x1: Int
                val x2: Int
                val x3: Int
                val m = -s
                val t = m / w.toFloat()
                val z1 = t + 2.5f
                val k1 = (z1 / 3f).dec.toInt()
                x1 = (3 * k1 - 1) * w + s
                val z2 = t + 1.5f
                val k2 = (z2 / 3f).dec.toInt()
                x2 = 3 * (k2) * w + s
                val z3 = t + 0.5f
                val k3 = (z3 / 3f).dec.toInt()
                x3 = (3 * k3 + 1) * w + s

                p1.place(x1, 0)
                p2.place(x2, 0)
                p3.place(x3, 0)
            }
        }
    }
}

