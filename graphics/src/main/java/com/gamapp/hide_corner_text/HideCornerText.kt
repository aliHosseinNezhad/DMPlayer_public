package com.gamapp.hide_corner_text

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.graphics.withClip
import androidx.lifecycle.*
import com.gamapp.layout.LayoutWithConstraints
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val TAG = "HideCornerTextTAG"

data class Enabled(
    val resume: Boolean,
    val enabledByUser: Boolean,
    val ready: Boolean
)

data class TextData(
    val text: String,
    val color: Color,
    val center: Boolean,
    val width: Float,
    val height: Float
)

@Composable
fun HideCornerText(
    text: String,
    center: Boolean,
    color: Color,
    enabled: State<Boolean>,
    modifier: Modifier = Modifier,
    state: HideCornerTextState = rememberHideCornerState()
) {
    val currentText = rememberUpdatedState(newValue = text)
    val currentColor = rememberUpdatedState(newValue = color)
    val currentCenter = rememberUpdatedState(newValue = center)
    val lifecycle = LocalLifecycleOwner.current
    var lifecycleResumed by remember {
        mutableStateOf(false)
    }
    var ready by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = lifecycle) {
        lifecycle.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                lifecycleResumed = source.lifecycle.currentState == Lifecycle.State.RESUMED
            }
        })
    }

    LayoutWithConstraints(modifier = Modifier.then(modifier)) {
        val width = constraints.value.maxWidth
        val height = constraints.value.maxHeight
        val scope = rememberCoroutineScope()
        DisposableEffect(text, state, width, height, color, center) {
            ready = false
            if (state.animatable.isRunning) {
                scope.launch {
                    state.stop(text, color, width.toFloat(), height.toFloat(), center)
                    delay(10)
                    ready = true
                }
            } else {
                state.init(text, color, width.toFloat(), height.toFloat(), center)
                scope.launch {
                    delay(10)
                    ready = true
                }
            }
            onDispose {}
        }
        LaunchedEffect(state) {
            val data by derivedStateOf {
                Enabled(
                    resume = lifecycleResumed,
                    enabledByUser = enabled.value,
                    ready = ready
                )
            }
            snapshotFlow {
                data
            }.collectLatest {
                Log.i(TAG, "HideCornerText: $it")
                if (!it.enabledByUser || !it.resume) {
                    state.stop(
                        currentText.value,
                        currentColor.value,
                        constraints.value.maxWidth.toFloat(),
                        constraints.value.maxHeight.toFloat(),
                        currentCenter.value
                    )
                } else if (it.ready && it.enabledByUser && it.resume) {
                    state.animation(
                        currentText.value,
                        currentColor.value,
                        constraints.value.maxWidth.toFloat(),
                        constraints.value.maxHeight.toFloat()
                    )
                }
            }
        }
        Canvas(modifier = Modifier.fillMaxSize()) {
            val rect = this.drawContext.size.toRect().toAndroidRect()
            drawIntoCanvas {
                with(it.nativeCanvas) {
                    val textWidth = state.paint.measureText(text)
                    val textHeight: Float = state.paint.descent() - state.paint.ascent()
                    val textOffset: Float = textHeight / 2f - state.paint.descent()
                    withClip(clipRect = rect) {
                        if (state.count + textWidth >= 0) {
                            drawText(
                                text,
                                0,
                                text.length,
                                1f * state.count,
                                height / 2f + textOffset / 2f,
                                state.paint
                            )
                        }
                        if (width < textWidth && width >= state.count + textWidth + width / 4)
                            drawText(
                                text,
                                0,
                                text.length,
                                1f * state.count + state.paint
                                    .measureText(text) + width / 4,
                                height / 2f + textOffset / 2f,
                                state.paint
                            )
                    }
                }
            }
        }
    }
}