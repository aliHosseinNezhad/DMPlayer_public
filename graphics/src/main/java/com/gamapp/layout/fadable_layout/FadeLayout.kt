package com.gamapp.layout.fadable_layout

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy


@Composable
fun rememberFadeLayoutMeasurePolicy(alpha: State<Float>) = remember {
    MeasurePolicy { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        val cw = placeables.maxOfOrNull { it.width } ?: 0
        val ch = placeables.maxOfOrNull { it.height } ?: 0
        val width = cw.coerceIn(constraints.minWidth, constraints.maxWidth)
        val height = ch.coerceIn(constraints.minHeight, constraints.maxHeight)
        layout(width, height) {
            placeables.forEach {
                it.placeWithLayer(0, 0) {
                    this.alpha = alpha.value
                }
            }
        }
    }
}


@Composable
inline fun FadeLayout(
    show: Boolean,
    modifier: Modifier = Modifier,
    noinline transitionSpec:
    @Composable Transition.Segment<Boolean>.() -> FiniteAnimationSpec<Float> = { tween(500) },
    label: String = "FadeLayout",
    content: @Composable () -> Unit
) {
    val transition = updateTransition(targetState = show, label = label)
    val alpha =
        transition.animateFloat(transitionSpec = transitionSpec, label = "$label/fadeValue") {
            if (it) 1f else 0f
        }
    val finalShow = remember {
        derivedStateOf {
            alpha.value != 0f
        }
    }
    Layout(
        modifier = modifier,
        measurePolicy = rememberFadeLayoutMeasurePolicy(alpha = alpha),
        content = {
            if (finalShow.value)
                content()
        })
}



