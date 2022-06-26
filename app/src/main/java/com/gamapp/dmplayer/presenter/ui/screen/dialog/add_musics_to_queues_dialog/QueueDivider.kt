package com.gamapp.dmplayer.presenter.ui.screen.dialog.add_musics_to_queues_dialog

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gamapp.dmplayer.presenter.ui.theme.onContent

@Composable
fun QueueDivider() {
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .padding(horizontal = 8.dp)
            .height(1.dp),
        thickness = 1.dp,
        color = MaterialTheme.colors.onContent.copy(0.05f)
    )
}