package com.gamapp.dmplayer.presenter.ui.screen.ext.text_field

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ErrorText(error: TextFieldState.Error) {
    Text(
        text = error.message.string(),
        modifier = Modifier
            .padding(horizontal = 14.dp)
            .heightIn(min = 0.dp, max = 60.dp)
            .fillMaxWidth(),
        style = MaterialTheme.typography.subtitle1,
        color = MaterialTheme.colors.error,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
    )
}