package com.gamapp.dmplayer.presenter.ui.screen.ext.text_field

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TitleText(model: Title) {
    if (model.show)
        Text(
            text = model.name.string(),
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .heightIn(min = 0.dp, max = 60.dp)
                .fillMaxWidth(),
            style = MaterialTheme.typography.subtitle1,
            color = LocalContentColor.current.copy(ContentAlpha.medium),
            maxLines = 1
        )
}