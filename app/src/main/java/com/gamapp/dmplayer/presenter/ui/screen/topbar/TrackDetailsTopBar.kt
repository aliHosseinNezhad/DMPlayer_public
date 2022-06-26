package com.gamapp.dmplayer.presenter.ui.screen.topbar

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gamapp.custom.CustomIconButton
import com.gamapp.dmplayer.R

@Composable
inline fun TrackDetailsTitleTopBar(
    title: String,
    noinline onBackPress: () -> Unit,
    content: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(horizontal = 16.dp)
    ) {
        CustomIconButton(
            onClick = onBackPress, modifier = Modifier
                .size(30.dp)
                .align(CenterVertically)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_down),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer {
                        rotationZ = 90f
                    },
                tint = MaterialTheme.colors.onBackground
            )
        }
        Text(
            text = title,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .widthIn(max = 150.dp)
                .wrapContentWidth()
                .align(CenterVertically),
            style = MaterialTheme.typography.h6
        )
        /**
         * other contents
         * */
        content()
    }
}