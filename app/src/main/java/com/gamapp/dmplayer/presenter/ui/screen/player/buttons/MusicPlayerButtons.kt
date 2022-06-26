package com.gamapp.dmplayer.presenter.ui.screen.player.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.dmplayer.presenter.ui.screen.player.buttons.elements.*
import com.gamapp.dmplayer.presenter.viewmodel.musicplayer.PlayerViewModel


@Composable
fun MusicPlayerButtons(
    modifier: Modifier,
    clickable: State<Boolean>,
    playViewModel: PlayerViewModel = hiltViewModel(),
) {
    val isPlaying = playViewModel.isPlaying.collectAsState()
    val repeatMode = playViewModel.repeatMode.collectAsState()
    val shuffle = playViewModel.shuffle.collectAsState()
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ShuffleButton(
            modifier = Modifier.requiredSize(60.dp),
            clickable = clickable,
            shuffleUseCase = playViewModel.playerInteracts.shuffleUseCase,
            shuffle = shuffle
        )
        FastRewindButton(
            modifier = Modifier.requiredSize(60.dp),
            clickable = clickable,
            rewindUseCase = playViewModel.playerInteracts.fastRewindUseCase
        )
        PlayPauseButton(
            modifier = Modifier.requiredSize(60.dp),
            isPlaying = isPlaying,
            playPauseUseCase = playViewModel.playerInteracts.playPauseUseCase,
            clickable = clickable
        )
        FastForward(
            modifier = Modifier.requiredSize(60.dp),
            clickable = clickable,
            fastForwardUseCase = playViewModel.playerInteracts.fastForwardUseCase
        )
        RepeatModeButton(
            modifier = Modifier.requiredSize(60.dp),
            repeatMode = repeatMode,
            clickable = clickable,
            repeatModeUseCase = playViewModel.playerInteracts.repeatModeUseCase
        )
    }
}
