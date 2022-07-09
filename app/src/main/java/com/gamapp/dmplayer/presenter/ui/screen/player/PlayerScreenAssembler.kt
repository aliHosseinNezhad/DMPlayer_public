package com.gamapp.dmplayer.presenter.ui.screen.player

import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.MaterialTheme
import androidx.compose.material.swipeable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.playlist.PlayList
import com.gamapp.dmplayer.presenter.ui.screen.player.buttons.MusicPlayerButtonsMinimal
import com.gamapp.dmplayer.presenter.ui.screen.player.buttons.PlayerMenuItems
import com.gamapp.layout.CrossFadeLayout
import com.gamapp.dmplayer.presenter.ui.screen.ext.TrackPlayer
import com.gamapp.dmplayer.presenter.ui.screen.ext.TrackPlayerLayout
import com.gamapp.dmplayer.presenter.utils.navigationBarHeight
import com.gamapp.dmplayer.presenter.utils.statusBarHeight
import com.gamapp.dmplayer.presenter.viewmodel.musicplayer.PlayerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


const val TAG = "playerScreenAssembler"

class PlayerShape(val value: Float, private val bottom: Dp) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val bottomMargin = with(density) { bottom.toPx() }
        val minWidth = with(density) { 60.dp.toPx() }
        val currentWidth = value * size.height + (1 - value) * (minWidth)
        val radius = minWidth / 2f * (1 - value)
        val top = size.height - currentWidth - (1 - value) * bottomMargin
        val bottom = size.height - (1 - value) * bottomMargin
        val offset2 = Offset(size.width, bottom)
        val offset1 = Offset(0f, top)
        val rect = Rect(offset1, offset2)
        val roundRect = RoundRect(rect, radiusX = radius, radiusY = radius)
        return Outline.Generic(Path().apply {
            addRoundRect(roundRect = roundRect)
        })
    }
}

@Composable
fun TrackPlayerScreen(nav: NavHostController) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val x = constraints.maxWidth
        val y = constraints.maxHeight
        val intSize = remember(x, y) {
            IntSize(x, y)
        }
        PlayerScreenAssembler(intSize, nav)
    }
}


@Composable
fun PlayerScreenAssembler(
    intSize: IntSize,
    nav: NavHostController
) {
    val playerViewModel: PlayerViewModel = hiltViewModel()
    val density = LocalDensity.current
    val intSizeState = rememberUpdatedState(newValue = intSize)
    val playerData = remember {
        derivedStateOf {
            PlayerData(
                size = intSizeState,
                density = density
            )
        }
    }
    val state = remember {
        PlayerState(
            dataState = playerData
        )
    }.apply {
        Init()
    }
    LaunchedEffect(key1 = state) {
        Log.i(TAG, "${state.hashCode()}")
    }
    val data by state.dataState
    val animationScope = rememberCoroutineScope {
        Dispatchers.Main
    }

    FullScreen(status = data.expandedState)
    val isPlaying by playerViewModel.isPlaying.collectAsState(false)
    val currentTrack by playerViewModel.currentTrack.collectAsState()
    val context = LocalContext.current
    val backColorEnabled = remember {
        derivedStateOf {
            isPlaying && playerViewModel.isDynamicGradientEnable.value
        }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { currentTrack }.collectLatest {
            playerViewModel.setUpWithTrack(context, it)
        }
    }
    com.gamapp.BackHandler(enabled = data.expandedState) {
        animationScope.launch {
            data.swipeableState.animateTo(Minimized)
        }
    }
    val navigationHeight = navigationBarHeight()
    val showPlayerContent = remember {
        derivedStateOf {
            !data.showPlayList
        }
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .graphicsLayer {
            translationY = 60.dp.toPx() * (1 - data.playerAlpha)
            alpha = data.playerAlpha
            clip = true
            shape = PlayerShape(data.motionValue, navigationHeight)
        }
        .swipeable(
            state = data.swipeableState,
            anchors = data.anchors,
            orientation = Orientation.Vertical,
            thresholds = { _, _ ->
                FractionalThreshold(0.3f)
            },
            reverseDirection = true,
            velocityThreshold = 200.dp,
            enabled = data.enableSwipeableState
        )
        .background(MaterialTheme.colors.surface)
    ) {
        TrackPlayerLayout(
            modifier = Modifier
                .fillMaxSize(),
            bottom = navigationBarHeight(),
            top = statusBarHeight(),
            data = data
        ) {
            BackColor(
                enabled = backColorEnabled,
                modifier = Modifier.layoutId(TrackPlayer.DynamicGradient),
                state = data
            )
            PlayerMenuItems(
                modifier = Modifier
                    .layoutId(TrackPlayer.Menu)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(60.dp),
                nav = nav,
                state = data
            )
            MusicPlayControllers(
                modifier = Modifier
                    .layoutId(TrackPlayer.Controller)
                    .wrapContentHeight()
                    .graphicsLayer {
                        alpha = data.playerExpandContentAlpha
                    }
            )
            MusicPlayerButtonsMinimal(
                modifier = Modifier
                    .layoutId(TrackPlayer.MinimalController)
                    .fillMaxWidth()
                    .height(60.dp),
                state = state
            )
            CrossFadeLayout(
                modifier = Modifier
                    .layoutId(TrackPlayer.TrackContent),
                one = {
                    PlayerTrackContent(state = data)
                },
                two = {
                    PlayList(
                        modifier = Modifier
                            .fillMaxSize(),
                        state = state
                    )
                },
                animationSpec = tween(500),
                showOne = showPlayerContent
            )
        }
    }

}







