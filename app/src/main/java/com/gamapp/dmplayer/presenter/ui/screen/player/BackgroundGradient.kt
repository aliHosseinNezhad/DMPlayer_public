package com.gamapp.dmplayer.presenter.ui.screen.player

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.dmplayer.presenter.viewmodel.musicplayer.PlayerViewModel
import com.gamapp.dynamicgradient.DynamicGradient
import com.gamapp.dynamicgradient.DynamicGradientState
import com.gamapp.dynamicgradient.rememberDynamicGradientState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.max

@Composable
fun InitDynamicGradient(
    state: DynamicGradientState,
    enable: () -> Boolean,
    colors: () -> List<Color>
) {
    LaunchedEffect(key1 = Unit) {
        launch {
            snapshotFlow {
                colors()
            }.collectLatest {
                state.setColors(it[0], it[1])
            }
        }
        launch {
            snapshotFlow {
                enable()
            }.collectLatest {
                if (it) {
                    state.start()
                } else state.stop()
            }
        }
    }
}


@Composable
fun BackgroundGradient(
    enable: State<Boolean>,
    state: PlayerData,
) {
    val viewModel: PlayerViewModel = hiltViewModel()
    val colors by viewModel.colors
    val resumeMotion by remember {
        derivedStateOf {
            enable.value && state.motionValue == 1f
        }
    }
    val dynamicGradientState = rememberDynamicGradientState()
    DynamicGradient(state = dynamicGradientState, modifier = Modifier.fillMaxSize())
    InitDynamicGradient(
        state = dynamicGradientState,
        enable = { resumeMotion },
        colors = { colors })
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                alpha = state.playerShrinkContentAlpha
            }
            .drawWithContent {
                drawRect(colors[0])
                drawContent()
            }
    )
}


operator fun Color.plus(color: Color): Color {
    val ma = max(alpha, color.alpha)
    val sa = alpha + color.alpha
    val red = red * alpha / sa + color.red * color.alpha / sa
    val green = green * alpha / sa + color.green * color.alpha / sa
    val blue = blue * alpha / sa + color.blue * color.alpha / sa
    return Color(red, green, blue, ma)
}

fun Color.makeDarker(): Color {
    val luminance = luminance()
    return when {
        luminance > 0.6f -> {
            this + Color.Black.copy(luminance - 0.5f)
        }
        luminance < 0.3f -> {
            this + Color.White.copy(0.1f)
        }
        else -> this
    }
}


@Composable
fun BackColor(
    enabled: State<Boolean>,
    modifier: Modifier,
    state: PlayerData,
) {
    Box(modifier = modifier) {
        BackgroundGradient(
            enable = enabled,
            state = state
        )
    }
}





