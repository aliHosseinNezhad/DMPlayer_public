package com.gamapp.dmplayer.presenter.ui.screen.player

import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gamapp.dmplayer.presenter.viewmodel.musicplayer.PlayerViewModel
import com.gamapp.hide_corner_text.HideCornerText

@Composable
fun MusicDetails(
    modifier: Modifier,
    viewModel: PlayerViewModel = viewModel(),
    state: PlayerData
) {
    val screenData by viewModel.musicModel
    val enabled = remember {
        derivedStateOf {
            true
        }
    }
    Column(
        modifier = Modifier
            .then(modifier)
            .graphicsLayer {
                alpha = state.playerExpandContentAlpha
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HideCornerText(
            text = screenData.title,
            center = true,
            color = MaterialTheme.colors.onBackground.copy(0.8f),
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        HideCornerText(
            text = screenData.artist,
            center = true,
            color = MaterialTheme.colors.onBackground.copy(ContentAlpha.medium),
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
        )
    }
}


@Composable
fun MinimalMusicDetails(modifier: Modifier,viewModel: PlayerViewModel = hiltViewModel()) {
    val screenData by viewModel.musicModel
    val enabled = remember {
        derivedStateOf {
            true
        }
    }
    Column(
        modifier = Modifier
            .then(modifier)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        HideCornerText(
            text = screenData.title,
            center = false,
            color = Color.White,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(20.dp)
        )
        HideCornerText(
            text = screenData.artist,
            center = false,
            color = Color.White.copy(ContentAlpha.medium),
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(15.dp)
        )
    }
}