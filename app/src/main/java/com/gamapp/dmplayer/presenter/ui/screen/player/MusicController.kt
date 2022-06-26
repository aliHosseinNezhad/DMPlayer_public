package com.gamapp.dmplayer.presenter.ui.screen.player


import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.dmplayer.presenter.ui.screen.player.buttons.MusicPlayerButtons
import com.gamapp.dmplayer.presenter.ui.screen.player.buttons.elements.TimeLine
import com.gamapp.dmplayer.presenter.viewmodel.musicplayer.PlayerViewModel


@Composable
fun MusicPlayControllers(
    modifier: Modifier,
) {
    val player = hiltViewModel<PlayerViewModel>()
    val seek = player.currentPosition.collectAsState()
    val duration = player.duration.collectAsState()
    Column(modifier = modifier) {
        TimeLine(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth()
                .height(70.dp)
                .wrapContentHeight(align = Alignment.CenterVertically),
            enabled = remember {
                mutableStateOf(true)
            },
            seek = seek,
            duration = duration,
            seekBarUseCase = player.playerInteracts.seekBarUseCase
        )
        Spacer(modifier = Modifier.padding(0.dp))
        MusicPlayerButtons(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 16.dp),
            clickable = remember {
                mutableStateOf(true)
            }
        )
    }
}

