package com.gamapp.dmplayer.presenter.ui.screen.topbar

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gamapp.custom.CustomIconButton
import com.gamapp.dmplayer.R

@Composable
fun BoxScope.TitleBar(title: String, onBackPress: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopCenter)
            .padding(horizontal = 16.dp)
            .height(90.dp)
    ) {
        CustomIconButton(
            onClick = onBackPress, modifier = Modifier
                .size(30.dp)
                .align(Alignment.CenterVertically)
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
                .align(Alignment.CenterVertically)
                .weight(1f)
                .padding(start = 16.dp)
                .wrapContentWidth(align = Alignment.Start)
                .wrapContentHeight(),
            fontSize = 22.sp,
            fontWeight = FontWeight(450),
            color = MaterialTheme.colors.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}