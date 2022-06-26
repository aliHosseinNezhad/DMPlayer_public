package com.gamapp.dmplayer.presenter.ui.screen.player.buttons.menu_buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.gamapp.custom.CustomIconButton
import com.gamapp.dmplayer.R


@Composable
fun SpeakerButton() {
    CustomIconButton(
        onClick = {}, modifier = Modifier
            .size(50.dp)
    ) {
        Image(
            painter = rememberVectorPainter(ImageVector.vectorResource(id = R.drawable.round_volume_up_24)),
            contentDescription = null,
            modifier = Modifier.size(25.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground.copy(0.8f))
        )
    }
}