package com.gamapp.dmplayer.presenter.ui.screen.player.buttons.elements

import android.util.Log
import androidx.compose.foundation.interaction.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.gamapp.domain.usecase.player.SeekBarUseCase
import com.gamapp.slider.Slider
import com.gamapp.slider.SliderColors
import com.gamapp.slider.SliderState
import com.gamapp.timeline.TimeLineState
import kotlinx.coroutines.flow.collectLatest

sealed class InteractionType {
    object Drag : InteractionType()
    object Press : InteractionType()
    object None : InteractionType()
}

const val TAG = "TimeLineTAG"

@Composable
fun TimeLine(
    modifier: Modifier,
    enabled: State<Boolean>,
    seek: State<Long>,
    duration: State<Long>,
    seekBarUseCase: SeekBarUseCase
) {

    val interactionSource = remember {
        MutableInteractionSource()
    }
    var changeByUser by remember {
        mutableStateOf(false)
    }
    var seekBar by remember {
        mutableStateOf(0f)
    }

    val externalSeek by remember {
        derivedStateOf {
            val value =
                seek.value.toFloat() / duration.value.toFloat()
            if (value.isNaN()) 0f else value.coerceIn(0f, 1f)
        }
    }

    var previousInteraction = remember<InteractionType> {
        InteractionType.None
    }

    LaunchedEffect(interactionSource) {
        suspend fun end() {
            previousInteraction = InteractionType.None
            val current = (duration.value * seekBar).toLong()
            seekBarUseCase(current)
            changeByUser = false
        }
        interactionSource.interactions.collectLatest {
            Log.d (TAG, "TimeLine: $it")
            when (it) {
                is DragInteraction.Start -> {
                    previousInteraction = InteractionType.Drag
                    changeByUser = true
                }
                is DragInteraction.Cancel, is DragInteraction.Stop -> {
                    end()
                }
                is PressInteraction.Press -> {
                    previousInteraction = InteractionType.Press
                    changeByUser = true
                }
                is PressInteraction.Release, is PressInteraction.Cancel -> {
                    if (previousInteraction != InteractionType.Drag) {
                        end()
                    }
                }
            }
        }
    }

    val value = remember {
        derivedStateOf {
            if (changeByUser) seekBar else externalSeek
        }
    }
    val currentTime = remember {
        derivedStateOf {
            if (changeByUser) (seekBar * duration.value).toLong() else seek.value
        }
    }
    com.gamapp.timeline.TimeLine(
        state = remember {
            TimeLineState(
                value = value,
                enabled = enabled,
                onValueChange = {
                    seekBar = it
                },
                duration = duration,
                current = currentTime,
                onFinallyValueChange = {}
            )
        },
        modifier = modifier,
        colors = SliderColors.colors(
            enabledThumbColor = Color.White,
            enabledSelectedLineColor = Color.White,
            enabledLineColor = Color.White.copy(0.4f)
        ),
        interactionSource = interactionSource
    )
//    TimeLine(
//        value = {
//            value
//        },
//        onValueChange = {
//            seekBar = it
//        },
//        enabled = clickable,
//        modifier = modifier,
//        colors = colors,
//        valueRange = 0f..1f,
//        duration = { duration.value.toInt() },
//        currentTime = { seek.value.toInt() },
//        interactionSource = interactionSource
//    )
}