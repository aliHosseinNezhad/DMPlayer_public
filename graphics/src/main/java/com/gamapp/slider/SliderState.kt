package com.gamapp.slider

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest




sealed class SliderEvents {
    object Drag : SliderEvents()
    object Fixed : SliderEvents()
}

open class SliderState(
    val value: State<Float>,
    internal val enabled: State<Boolean>,
    private val onValueChange: (Float) -> Unit,
    private val onFinallyValueChange: (Float) -> Unit = {},
) {
    internal val event: MutableState<SliderEvents> = mutableStateOf(SliderEvents.Fixed)
    internal val translation by value

    private val colors: MutableState<SliderColors?> = mutableStateOf(null)

    private val thumbColor = colors.lazyDerivedState {
        if (enabled.value) it.enabledThumbColor else it.disabledThumbColor
    }
    private val lineColor = colors.lazyDerivedState {
        if (enabled.value) it.enabledLineColor else it.disabledLineColor
    }
    private val selectedLineColor = colors.lazyDerivedState {
        if (enabled.value) it.enabledSelectedLineColor else it.disabledSelectedLineColor
    }

    internal val linePaint by lineColor.lazyDerivedState {
        Paint().apply {
            this.color = it
            isAntiAlias = true
        }
    }
    internal val thumbPaint by thumbColor.lazyDerivedState {
        Paint().apply {
            this.color = it
            isAntiAlias = true
        }
    }
    internal val selectLinePaint by selectedLineColor.lazyDerivedState {
        Paint().apply {
            this.color = it
            isAntiAlias = true
        }
    }

    internal val animator = Animatable(0f)

    internal fun onDrag(ds: Float) {
        val v = (translation + ds).coerceIn(0f, 1f)
        onValueChange(v)
    }

    internal fun onDragEnd() {
        onFinallyValueChange(value.value)
    }

    internal fun setOffset(pos: Float, width: Int) {
        val v = (pos / width.toFloat()).coerceIn(0f, 1f)
        onValueChange(v)
    }

    @Composable
    fun Init(colors: SliderColors) {
        this.colors.apply {
            value = colors
        }
        LaunchedEffect(key1 = Unit) {
            val isMoving = derivedStateOf {
                event.value !is SliderEvents.Fixed
            }
            snapshotFlow {
                isMoving.value
            }.collectLatest {
                val v = if (it) 1f else 0f
                animator.animateTo(v, animationSpec = tween(300))
            }
        }
    }

    fun onTapEnd() {
        onFinallyValueChange(value.value)
    }


}

fun <T, V> State<V?>.lazyDerivedState(scope: (V) -> T): State<T> =
    LazyDerivedState(this, scope).value

class LazyDerivedState<T, V>(private val state: State<V?>, private val scope: (V) -> T) {
    val value: State<T> by lazy {
        val nonNullState = derivedStateOf {
            val v = state.value
            v ?: throw IllegalStateException("")
        }
        derivedStateOf {
            scope(nonNullState.value)
        }
    }
}