package com.gamapp.dmplayer.presenter.ui.screen.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gamapp.dmplayer.presenter.ui.screen.ext.Ref
import com.gamapp.dmplayer.presenter.ui.screen.ext.TrackPlayerScope
import com.gamapp.dmplayer.presenter.viewmodel.musicplayer.PlayerViewModel
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.models.emptyPlayList
import com.gamapp.pager.RecyclerHorizontalPager
import com.gamapp.pager.rememberRecyclerPagerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun TrackPlayerScope.TracksImagePager(
    modifier: Modifier,
    state: PlayerData,
    viewModel: PlayerViewModel = hiltViewModel(),
    bound: State<DpSize>,
) {
    val scope = rememberCoroutineScope {
        Dispatchers.Main
    }
    val isMotionPercentZero by remember {
        derivedStateOf {
            state.motionValue == 0f
        }
    }
    val playList by viewModel.playList.collectAsState(emptyPlayList())
    val tracks by remember {
        derivedStateOf {
            playList.order
        }
    }
    val currentTrack by viewModel.currentTrack.collectAsState()
    val selectedIndex by remember {
        derivedStateOf {
            tracks.indexOf(currentTrack)
        }
    }
    val check by remember {
        derivedStateOf {
            selectedIndex >= 0
                    && tracks.isNotEmpty()
                    && selectedIndex < tracks.size
                    && currentTrack != null
                    && tracks.contains(currentTrack)
        }
    }
    Box(modifier = modifier) {
        if (check) {
            val clickModifier =
                if (isMotionPercentZero) Modifier.clickable(interactionSource = remember {
                    MutableInteractionSource()
                }, indication = null) {
                    scope.launch {
                        state.swipeableState.animateTo(Expanded)
                    }
                } else Modifier
            val recyclerState = rememberRecyclerPagerState(initItem = currentTrack!!, data = tracks)
            LaunchedEffect(key1 = recyclerState) {
                recyclerState.currentPageIndex.collect {
                    if (it.second && it.first != currentTrack)
                        scope.launch {
                            viewModel.setPlayerListAndCurrent(it.first, tracks)
                        }
                }
            }
            LaunchedEffect(key1 = currentTrack, key2 = tracks) {
                val track = currentTrack ?: return@LaunchedEffect
                recyclerState.skipToPage(track)
            }
            RecyclerHorizontalPager(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .then(modifier)
                    .then(clickModifier),
                state = recyclerState
            ) {
                TrackImage(
                    bound = bound,
                    trackModel = it,
                    state = state,
                    motionHeight = motionHeight
                )
            }
        }
    }

}

@Composable
fun TrackImage(
    trackModel: TrackModel,
    bound: State<DpSize>,
    state: PlayerData,
    motionHeight: Ref<Int>,
) {
    val width = bound.value.width
    val height = bound.value.height
    val viewModel = viewModel<PlayerViewModel>()
    val playingTrack = viewModel.currentTrack.collectAsState()
    val current = rememberUpdatedState(newValue = trackModel)
    val isSelected by remember {
        derivedStateOf {
            current.value == playingTrack.value
        }
    }
    val translationY by remember {
        derivedStateOf {
            motionHeight.value * (1 - state.motionValue)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val clipModifier = if (isSelected)
            Modifier
                .graphicsLayer {
                    this.translationY = translationY
                }
                .graphicsLayer {
                    transformOrigin = TransformOrigin(0f, 0f)
                    val dif = (width - height)
                    val m1 =
                        (32.dp + if (dif < 32.dp) 32.dp - dif else 0.dp) * (state.motionValue)
                    val m2 = 16.dp * (1 - state.motionValue)
                    this.translationY = (m1.toPx() + m2.toPx()) / 2f
                    translationX =
                        (dif.toPx() / 2f) * state.motionValue + this.translationY
                    val scale =
                        state.motionValue * (height - m1).toPx() / height.toPx() + (1 - state.motionValue) * (60.dp - m2).toPx() / height.toPx()
                    scaleY = scale
                    scaleX = scale
                    //
                    clip = true
                    shape = RoundedCornerShape(
                        height / 2f * (1 - state.motionValue) + height / 8f * state.motionValue
                    )
                }
        else
            Modifier
                .graphicsLayer {
                    transformOrigin = TransformOrigin(0f, 0f)
                    val dif = (width - height).coerceAtLeast(0.dp)
                    val m1 =
                        (32.dp + if (dif < 32.dp) 32.dp - dif else 0.dp) * (state.motionValue)
                    this.translationY = (m1.toPx()) / 2f
                    translationX =
                        (dif.toPx() / 2f) * 1f + this.translationY
                    val scale = (height - m1) / height
                    scaleY = scale
                    scaleX = scale
                }
                .graphicsLayer {
                    clip = true
                    shape = RoundedCornerShape(
                        height / 8f
                    )
                }
        val density = LocalDensity.current
        val size by rememberUpdatedState(newValue = with(density) { width.roundToPx() })
        AsyncTrackPagerImage(
            id = trackModel.id,
            size = size,
            modifier = Modifier
                .size(height)
                .then(clipModifier)
                .drawWithContent {
                    drawRect(Color.Black.copy(0.6f))
                    drawContent()
                },
            state = state
        )
    }
}

@Composable
fun Float.toDp(): Dp {
    with(LocalDensity.current) {
        return this@toDp.toDp()
    }
}

@Composable
fun Dp.toPx(): Float {
    with(LocalDensity.current) {
        return this@toPx.toPx()
    }
}



