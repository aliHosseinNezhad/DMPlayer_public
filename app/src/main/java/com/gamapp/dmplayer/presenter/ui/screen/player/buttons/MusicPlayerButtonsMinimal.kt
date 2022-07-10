package com.gamapp.dmplayer.presenter.ui.screen.player.buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.custom.CustomIconButton
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.screen.player.Expanded
import com.gamapp.dmplayer.presenter.ui.screen.player.MinimalMusicDetails
import com.gamapp.dmplayer.presenter.ui.screen.player.MinimalPlayerHeight
import com.gamapp.dmplayer.presenter.ui.screen.player.PlayerState
import com.gamapp.dmplayer.presenter.ui.screen.player.buttons.elements.SkipToNextButton
import com.gamapp.dmplayer.presenter.ui.screen.player.buttons.elements.SkipToBackButton
import com.gamapp.dmplayer.presenter.ui.screen.player.buttons.elements.PlayPauseButton
import com.gamapp.dmplayer.presenter.viewmodel.musicplayer.PlayerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun MusicPlayerButtonsMinimal(
    modifier: Modifier,
    state: PlayerState,
) {
    val clickable = remember {
        mutableStateOf(true)
//        derivedStateOf {
//            state.data.expanded || state.data.motionValue != 1f
//        }
    }
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .then(modifier)
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null, enabled = clickable.value
            ) {
                scope.launch {
                    state.data.swipeableState.animateTo(0)
                }
            }
            .padding(start = MinimalPlayerHeight.dp)
            .graphicsLayer {
                alpha = state.data.minimalButtonsAlpha
                val offset = 60.dp.toPx()
                translationY = state.data.motionValue * offset
            },
        verticalAlignment = CenterVertically
    ) {
        MinimalMusicDetails(modifier = Modifier.weight(1f))
        MinimalButtons(clickable = clickable, state = state)
        Spacer(modifier = Modifier.padding(start = 16.dp))
    }
}

@Composable
fun MinimalButtons(clickable: State<Boolean>, state: PlayerState) {
    val scope = rememberCoroutineScope()
    val player: PlayerViewModel = hiltViewModel()
    val isPlaying = player.isPlaying.collectAsState(false)
    Row(
        modifier = Modifier
            .fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SkipToBackButton(
            modifier = Modifier
                .size(45.dp)
                .align(CenterVertically),
            clickable = clickable,
            skipToPreviousUseCase = player.playerInteracts.skipToPrevious,
            rewindUseCase = player.playerInteracts.rewind
        )
        PlayPauseButton(
            modifier = Modifier
                .size(45.dp)
                .align(CenterVertically),
            isPlaying = isPlaying,
            playPauseUseCase = player.playerInteracts.playPause,
            clickable = clickable
        )
        SkipToNextButton(
            modifier = Modifier
                .size(45.dp)
                .align(CenterVertically),
            clickable = clickable,
            skipToNextUseCase = player.playerInteracts.skipToNext,
            forwardUseCase = player.playerInteracts.forward
        )
        CustomIconButton(
            enabled = clickable.value,
            onClick = {
                scope.launch {
                    state.data.swipeableState.animateTo(Expanded)
                    delay(30)
                    state.data.showPlayList = true
                }
            },
            modifier = Modifier
                .size(45.dp)
                .align(CenterVertically)
                .clip(CircleShape),
        ) {
            Image(
                painter = rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.ic_queues)),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
        }
    }

}

