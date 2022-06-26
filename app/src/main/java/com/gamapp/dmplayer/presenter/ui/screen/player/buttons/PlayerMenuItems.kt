package com.gamapp.dmplayer.presenter.ui.screen.player.buttons

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gamapp.dmplayer.presenter.ui.screen.player.PlayerData
import com.gamapp.dmplayer.presenter.ui.screen.player.buttons.menu_buttons.MinimizeButton
import com.gamapp.dmplayer.presenter.ui.screen.player.buttons.menu_buttons.SettingButton
import com.gamapp.dmplayer.presenter.ui.screen.player.buttons.menu_buttons.SpeakerButton
import com.gamapp.dmplayer.presenter.ui.screen.player.buttons.menu_buttons.TimerButton


@Composable
fun PlayerMenuItems(
    modifier: Modifier,
    nav: NavHostController,
    state: PlayerData
) {
    Row(modifier = modifier.graphicsLayer {
        alpha = state.playerExpandContentAlpha
    }, verticalAlignment = Alignment.CenterVertically) {
        MinimizeButton(data = state)
        Spacer(modifier = Modifier.weight(1f))
        TimerButton()
        Spacer(modifier = Modifier.width(2.dp))
        SpeakerButton()
        Spacer(modifier = Modifier.width(2.dp))
        SettingButton(nav = nav)
    }
}

