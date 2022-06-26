package com.gamapp.dmplayer.presenter.ui.screen.player.buttons.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.custom.CustomIconButton
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.screen.player.PlayerData
import com.gamapp.dmplayer.presenter.ui.screen.player.PlayerState
import com.gamapp.dmplayer.presenter.viewmodel.musicplayer.PlayerViewModel


@Composable
fun RowScope.QueueButton(state: PlayerData) {
    CustomIconButton(
        onClick = {
            state.showPlayList = !state.showPlayList
        },
        modifier = Modifier
            .requiredSize(50.dp)
            .align(Alignment.Bottom),
    ) {
        Icon(
            painter = rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.ic_queues)),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color.White,
        )
    }
}