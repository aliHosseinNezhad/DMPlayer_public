package com.gamapp.dmplayer.presenter.ui.screen.player.buttons.menu_buttons

import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.gamapp.custom.CustomIconButton
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.screen.player.Minimized
import com.gamapp.dmplayer.presenter.ui.screen.player.PlayerData
import com.gamapp.dmplayer.presenter.ui.screen.player.PlayerState
import kotlinx.coroutines.launch

@Composable
fun MinimizeButton(data: PlayerData) {
    val scope = rememberCoroutineScope()
    CustomIconButton(
        onClick = {
            scope.launch {
                if (data.showPlayList)
                    data.showPlayList = false
                data.swipeableState.animateTo(Minimized)
            }
        }, modifier = Modifier
            .size(50.dp)
    ) {
        Icon(
            painter = rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.ic_arrow_down_bold)),
            contentDescription = "minimizeButton",
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colors.onBackground.copy(0.8f),
        )
    }
}