package com.gamapp.slider

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

private const val TAG = "SliderEventsTAG"
private const val Min = 0.2f
private const val Max = 1f
private val circleSize = 15.dp
private val height = 50.dp


@Preview
@Composable
fun PreSlider() {
    val state = remember {
        val value = mutableStateOf(0f)
        SliderState(
            value = value,
            enabled = mutableStateOf(true),
            onValueChange = {
                value.value = it
            }
        )
    }
    Slider(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.Center),
        state = state,
        colors = SliderColors.colors()
    )
}

@Composable
fun Slider(
    state: SliderState,
    interactionSource: MutableInteractionSource = remember {
        MutableInteractionSource()
    },
    colors: SliderColors = SliderColors.colors(),
    modifier: Modifier
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    state.Init(colors = colors)
    BoxWithConstraints(
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth()
            .padding(horizontal = circleSize / 2)
            .requiredHeight(height)
    ) {
        val constraints = this.constraints
        val maxWidth = constraints.maxWidth
        val draggableState = rememberDraggableState {
            state.onDrag((it / maxWidth))
        }
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
                .sliderTapModifier(
                    draggableState = draggableState,
                    interactionSource = interactionSource,
                    maxPx = maxWidth.toFloat(),
                    isRtl = isRtl,
                    state = state,
                    enabled = state.enabled
                )
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    reverseDirection = isRtl,
                    interactionSource = interactionSource,
                    onDragStarted = {
                        state.event.value = SliderEvents.Drag
                        state.setOffset(it.x, maxWidth)
                    },
                    onDragStopped = {
                        state.event.value = SliderEvents.Fixed
                        state.onDragEnd()
                    },
                    enabled = state.enabled.value
                )
                .requiredHeight(height)
                .wrapContentHeight(align = Alignment.CenterVertically)
                .requiredHeight(circleSize)
        ) {
            Line(state = state)
            Thumb(state = state, constraints = constraints)
        }
    }
}

@SuppressLint("UnnecessaryComposedModifier")
private fun Modifier.sliderTapModifier(
    draggableState: DraggableState,
    interactionSource: MutableInteractionSource,
    maxPx: Float,
    isRtl: Boolean,
    state: SliderState,
    enabled: State<Boolean>
) = composed(
    factory = {
        if (enabled.value) {
            pointerInput(draggableState, interactionSource, maxPx, isRtl) {
                detectTapGestures(
                    onPress = { pos ->
                        val to = if (isRtl) maxPx - pos.x else pos.x
                        val pressInteraction = PressInteraction.Press(pos)
                        state.setOffset(to, maxPx.toInt())
                        interactionSource.tryEmit(pressInteraction)
                        try {
                            awaitRelease()
                            interactionSource.tryEmit(PressInteraction.Release(pressInteraction))
                            state.onTapEnd()
                        } catch (_: GestureCancellationException) {
                            interactionSource.tryEmit(PressInteraction.Cancel(pressInteraction))
                            state.onTapEnd()
                        }
                    }
                )
            }
        } else {
            this
        }
    })

@Composable
fun Line(state: SliderState) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
    ) {
        val v = state.animator.value
        val value = v * Max + (1 - v) * Min
        val rect = size.toRect()
        val trans = (rect.bottom - rect.top) * value
        val r = rect.copy(bottom = trans + rect.top)
        val radius = r.minDimension / 2f
        translate(top = size.height / 2 - r.height / 2) {
            drawIntoCanvas {
                it.drawRoundRect(
                    left = r.left,
                    top = r.top,
                    bottom = r.bottom,
                    right = r.right,
                    radiusY = radius,
                    radiusX = radius,
                    paint = state.linePaint
                )
            }
        }
    }
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .height(20.dp)
    ) {
        val rect = size.toRect()
        val v = state.animator.value
        val value = v * Max + (1 - v) * Min
        val trans = (rect.bottom - rect.top) * value
        val r = rect.copy(bottom = trans + rect.top)
        val radius = r.minDimension / 2f
        val path = Path().apply {
            addRoundRect(RoundRect(r, radius, radius))
        }
        translate(top = size.height / 2 - r.height / 2) {
            clipPath(path) {
                drawIntoCanvas {
                    it.drawRect(
                        rect = rect.progress(state.translation),
                        paint = state.selectLinePaint
                    )
                }
            }
        }
    }
}

fun Rect.progress(value: Float): Rect {
    return copy(right = left + (right - left) * value)
}


@Composable
fun Thumb(state: SliderState, constraints: Constraints) {
    Canvas(modifier = Modifier
        .graphicsLayer {
            val maxPx = constraints.maxWidth
            translationX =
                (maxPx * state.translation) - circleSize.toPx() / 2f
            val v = 1 - state.animator.value
            alpha = v
            scaleX = v
            scaleY = v
        }
        .size(circleSize)) {
        drawIntoCanvas {
            it.drawCircle(
                center = center,
                radius = size.minDimension / 2f,
                paint = state.thumbPaint
            )
        }
    }
}