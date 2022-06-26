package com.gamapp.dmplayer.presenter.ui.screen.elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gamapp.layout.rememberState

@Composable
fun LinearItem(
    title: String,
    subtitle: String,
    imageId: Long?,
    defaultImage: Int,
    onItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
            .padding(horizontal = 16.dp)
            .height(75.dp)
    ) {
        ItemImage(
            id = imageId,
            defaultImage = defaultImage,
            modifier = Modifier
                .size(50.dp)
                .align(CenterVertically)
        )
        Spacer(modifier = Modifier.width(16.dp))
        DetailText(
            title = title,
            subtitle = subtitle,
            isSelected = false
        )
        Spacer(modifier = Modifier.width(16.dp))
        MoreVert(show = rememberState {
            false
        }) {

        }
    }
}