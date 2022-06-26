package com.gamapp.layout

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.util.fastForEach
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun <T> pair(items1: Iterator<T>, items2: Iterator<T>) {


}


fun main() {
    val one = "1"
    val two = "2"
    val three = "3"
    val map1 = mutableMapOf<Any, IntOffset>()
    val map2 = mutableMapOf<Any, IntOffset>()
    map1[one] = IntOffset(0, 0)
    map1[two] = IntOffset(0, 1)
    map1[three] = IntOffset(0, 4)
    map2[one] = IntOffset(0, 0)
    map2[two] = IntOffset(0, 1)
    map2["3"] = IntOffset(0, 4)
    println(map1 == map2)
}

@Composable
fun rememberStateRowMeasurePolicy(items: State<List<RowData>>, state: RowState) = remember(state) {
    MeasurePolicy { measurables, constraints ->
        val childConstraint = constraints.copy(minWidth = 0, minHeight = 0)
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        val parentDataMap = buildMap(capacity = measurables.size) {
            measurables.fastForEach {
                this[it.layoutId] =
                    ((it.parentData as? RowColumnParentData) ?: RowColumnParentData())
            }
        }
        val placeables = buildMap(capacity = measurables.size) {
            measurables.fastForEach {
                this[it.layoutId] = it.measure(childConstraint)
            }
        }
        layout(width, height) {
            var off = 0
            var totalWeight = 0f
            var totalValue = 0
            items.value.forEach {
                if (it is RowData.Key) {
                    placeables[it.key]?.let {
                        totalValue += it.width
                    }
                } else if (it is RowData.Weight) {
                    totalWeight += it.value
                }
            }
            val leftValue = width - totalValue.coerceIn(0, width)
            val positions = mutableMapOf<Any, IntOffset>()
            items.value.forEach {
                if (it is RowData.Key) {
                    val data = parentDataMap[it.key]
                    val placeable = placeables[it.key]
                    if (placeable != null) {
                        positions[it.key] = IntOffset(off, 0)
                        off += placeable.width
                    }
                } else if (it is RowData.Weight) {
                    off += ((it.value / totalWeight) * leftValue).toInt()
                }
            }
            state.setPosition(positions)
            state.positionState.forEach { pair ->
                placeables[pair.key]?.let {
                    it.placeWithLayer(pair.value.offset.value) {
                        alpha = pair.value.alpha.value
                    }
                }
            }
        }
    }
}

sealed class RowData {
    class Weight(val value: Float) : RowData() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            val obj = other as? RowData.Weight ?: return false
            return obj.value == this.value
        }

        override fun hashCode(): Int {
            return this.value.hashCode()
        }
    }

    class Key(val key: Any) : RowData() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            val obj = other as? RowData.Key ?: return false
            return obj.key == this.key
        }

        override fun hashCode(): Int {
            return key.hashCode()
        }
    }
}

interface DataScope {
    fun key(value: Any)
    fun weight(value: Float)
}

class DataScopeImpl : DataScope {
    fun reset() {
        items = mutableListOf()
    }

    var items = mutableListOf<RowData>()
        private set

    override fun key(value: Any) {
        items += RowData.Key(value)
    }

    override fun weight(value: Float) {
        items += RowData.Weight(value)
    }
}

@Composable
fun <T> rememberState(vararg keys: Any, scope: () -> T) = remember(keys = keys) {
    mutableStateOf(scope())
}

@Composable
inline fun <T> rememberState(
    key1: Any? = null,
    crossinline scope: @DisallowComposableCalls () -> T
) = remember(key1 = key1) {
    mutableStateOf(scope())
}

class PlaceDataState(
    val offset: MutableState<IntOffset> = mutableStateOf(IntOffset.Zero),
    val alpha: MutableState<Float> = mutableStateOf(0f),
) {
    fun toPlaceData() = PlaceData(offset.value, alpha.value)
    override fun toString(): String {
        return toPlaceData().toString()
    }
}

data class PlaceData(
    val offset: IntOffset,
    val alpha: Float,
) {
    override fun toString(): String {
        return "PlaceData(offset:$offset, alpha:$alpha)"
    }
}


class RowState(val scope: CoroutineScope) {
    val positionState: SnapshotStateMap<Any, PlaceDataState> = mutableStateMapOf()
    val positions get() = positionState
    private var job: Job? = null
    private val animatable = Animatable(0f)
    private var requested: Map<Any, IntOffset>? = null
    private var transformer: List<Pair<Any, String>>? = null
    fun setPosition(target: Map<Any, IntOffset>) {
        if (requested != target)
            requested = target
        else return
        scope.launch {
            animatable.stop()
            job?.join()
            animatable.snapTo(0f)
            job = launch {
                val from = positions.keys.toList()
                val to = target.keys.toList()
                val sum = to + from
                transformer = sum.groupBy { it }.mapNotNull {
                    when (it.value.size) {
                        2 -> {
                            it.key to "both"
                        }
                        1 -> {
                            when (it.key) {
                                in from -> it.key to "from"
                                in to -> it.key to "to"
                                else -> null
                            }
                        }
                        else -> null
                    }
                }
                Log.i(TAG, "transformer:$transformer")
                val fromPosition = buildMap {
                    positions.forEach {
                        this[it.key] = PlaceData(it.value.offset.value, it.value.alpha.value)
                    }
                }
                Log.i(TAG, "setPosition: ${positions.size}")
                transformer?.fastForEach {
                    if (it.second == "to") {
                        positionState.getOrPut(it.first) {
                            PlaceDataState().apply {
                                offset.value = target[it.first]!!
                                alpha.value = 0f
                            }
                        }
                    }
                }
                Log.i(TAG, "currentPosition ${positions.size}")
                animatable.animateTo(1f, tween(1000)) {
                    transformer?.fastForEach { it ->
                        when (it.second) {
                            "both" -> {
                                val p1 = fromPosition[it.first]?.offset
                                val p2 = target[it.first]
                                if (p2 != null && p1 != null) {
                                    positionState[it.first]?.offset?.value =
                                        p1 + (p2 - p1) * value
                                    val a1 = fromPosition[it.first]?.alpha ?: 1f
                                    val a2 = 1f
                                    if (a1 != 1f) {
                                        positionState[it.first]?.alpha?.value =
                                            a1 + (a2 - a1) * value
                                    }
                                }
                            }
                            "from" -> {
                                val v1 = fromPosition[it.first]?.alpha ?: 1f
                                val v2 = 0f
                                positionState[it.first]?.alpha?.value = v1 + (v2 - v1) * value
                            }
                            "to" -> {
                                val v1 = 0f
                                val v2 = 1f
                                positionState[it.first]?.alpha?.value = v1 + (v2 - v1) * value
                            }
                        }
                    }
                }
                onEnd()
            }
        }
    }

    private fun onEnd() {
        Log.i(TAG, "to remove")
        transformer?.fastForEach {
            if (it.second == "from") {
                Log.i(TAG, "setPosition: remove")
                positionState.remove(it.first)
            }
        }
        this.transformer = null
    }
}

@Composable
fun rememberRowState(scope: CoroutineScope) = remember(scope) {
    RowState(scope = scope)
}

@Composable
inline fun StateRow(
    noinline data: DataScope.() -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    val dataScope = remember {
        DataScopeImpl()
    }
    val scope = rememberCoroutineScope()
    val state = rememberRowState(scope = scope)
    val itemsState = remember {
        mutableStateOf(listOf<RowData>())
    }
    LaunchedEffect(key1 = data) {
        dataScope.reset()
        data.invoke(dataScope)
        itemsState.value = dataScope.items
    }
    Layout(
        measurePolicy = rememberStateRowMeasurePolicy(itemsState, state = state),
        content = {
            RowScopeImpl.content()
        })
}