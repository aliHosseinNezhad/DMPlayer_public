package com.gamapp.dmplayer.presenter.ui.screen.listof.linear.playlist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList.basetracks.BaseTracks
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList.basetracks.TrackSortBarVisibility
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList.basetracks.rememberBaseTrackState
import com.gamapp.dmplayer.presenter.ui.screen.player.PlayerState
import com.gamapp.dmplayer.presenter.viewmodel.musicplayer.PlayerViewModel

@Composable
fun PlayList(modifier: Modifier, state: PlayerState) {
    val viewModel = viewModel<PlayerViewModel>()
    val playList by viewModel.playList.collectAsState(com.gamapp.domain.models.PlayList(emptyList()))
    val tracks = playList.order
    val baseTrackState = rememberBaseTrackState {
        sortBarVisibility = TrackSortBarVisibility.Invisible
        canHideTrackPlayer = false
    }
    val isDark = isSystemInDarkTheme()
    val isDarkTheme by rememberUpdatedState(newValue = isDark)
    val background by remember {
        derivedStateOf {
            if (isDarkTheme) Color.Black.copy(0.3f) else Color.White.copy(0.3f)
        }
    }
    BackHandler(state.data.showPlayList) {
        state.data.showPlayList = false
    }
    Column(modifier = modifier
        .padding(top = 16.dp)
        .padding(horizontal = 16.dp)
        .graphicsLayer {
            clip = true
            shape = RoundedCornerShape(25.dp)
        }
        .background(background)) {
        Text(
            text = "PlayList (${tracks.size})",
            color = MaterialTheme.colors.onSurface,
            fontSize = 17.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(60.dp)
                .wrapContentHeight(align = CenterVertically)
        )
        BaseTracks(
            items = tracks, onPlay = {}, popups = listOf(), modifier = Modifier
                .clip(RoundedCornerShape(25.dp))
                .weight(1f)
                .fillMaxWidth(),
            state = baseTrackState
        )
    }

}