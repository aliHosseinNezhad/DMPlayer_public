package com.gamapp.dmplayer.presenter.ui.screen.elements.empty_list

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp


@Composable
fun BoxScope.EmptyListImage(@DrawableRes icon: Int, @StringRes text: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.Center)
    ) {
        Icon(
            painter = rememberVectorPainter(image = ImageVector.vectorResource(id = icon)),
            contentDescription = "empty",
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.CenterHorizontally),
            tint = MaterialTheme.colors.onSurface.copy(0.5f)
        )
        Text(
            text = stringResource(text),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 8.dp)
                .wrapContentHeight(align = Alignment.CenterVertically)
                .padding(horizontal = 32.dp)
                .wrapContentWidth(align = Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onSurface.copy(0.5f),
            style = MaterialTheme.typography.subtitle2,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}