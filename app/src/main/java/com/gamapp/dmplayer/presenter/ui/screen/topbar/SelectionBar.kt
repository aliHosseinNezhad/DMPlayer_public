package com.gamapp.dmplayer.presenter.ui.screen.topbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gamapp.custom.CustomIconButton
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.TopBarType
import com.gamapp.dmplayer.presenter.ui.theme.content
import com.gamapp.switch.Switch

@Composable
fun SelectionCheckBox(modifier: Modifier, type: TopBarType.Selection) {
    Column(
        modifier = Modifier
            .then(modifier)
            .clickable(interactionSource = remember {
                MutableInteractionSource()
            }, indication = null) {
                type.checkbox(!type.isChecked.value)
            },
        verticalArrangement = Arrangement.Center
    ) {
        Switch(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .size(25.dp)
                .align(Alignment.CenterHorizontally),
            selectedColor = MaterialTheme.colors.primary,
            border = MaterialTheme.colors.content,
            isChecked = type.isChecked.value,
            onCheckedChange = {
                type.checkbox(it)
            }
        )
        Text(
            text = "all",
            modifier = Modifier
                .wrapContentWidth()
                .height(20.dp)
                .wrapContentHeight(align = Alignment.CenterVertically)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Start,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun BoxScope.SelectionBar(type: TopBarType.Selection) {
    Row(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .fillMaxWidth()
            .height(90.dp)
            .padding(horizontal = 16.dp)
    ) {
        CustomIconButton(
            onClick = {}, modifier = Modifier
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
        SelectionCheckBox(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(horizontal = 8.dp)
                .wrapContentWidth(align = Alignment.Start),
            type = type
        )
    }
}