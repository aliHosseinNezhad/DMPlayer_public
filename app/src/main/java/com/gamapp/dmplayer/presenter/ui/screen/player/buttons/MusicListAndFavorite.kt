package com.gamapp.dmplayer.presenter.ui.screen.player.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.gamapp.dmplayer.presenter.ui.screen.player.PlayerData
import com.gamapp.dmplayer.presenter.ui.screen.player.PlayerState
import com.gamapp.dmplayer.presenter.ui.screen.player.buttons.elements.QueueButton


@Composable
fun MusicListAndFavorite(modifier: Modifier, state: PlayerData) {
    Row(
        modifier = Modifier
            .then(modifier)
            .graphicsLayer {
                alpha = state.playerExpandContentAlpha
            },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        QueueButton(state)
        FavoriteButton()
        AddToQueueButton()
    }
}