package com.gamapp.dmplayer.presenter.ui.screen.player.buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.dialogs
import com.gamapp.dmplayer.presenter.ui.screen.dialog.showAddToQueueDialog
import com.gamapp.dmplayer.presenter.viewmodel.musicplayer.PlayerViewModel

@Composable
fun RowScope.AddToQueueButton() {
    val viewModel: PlayerViewModel = hiltViewModel()
    val dialog = dialogs()
    val scope = rememberCoroutineScope()
    CustomIconButton(
        onClick = {
            viewModel.currentTrack.value?.let {
                dialog.showAddToQueueDialog(listOf(it)) {}
            }
        },
        modifier = Modifier
            .requiredSize(50.dp)
            .align(Alignment.Bottom)
    ) {
        Icon(
            painter = rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.ic_add)),
            contentDescription = null,
            modifier = Modifier
                .size(20.dp),
            tint = Color.White
        )
    }
}