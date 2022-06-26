package com.gamapp.dmplayer.presenter.ui.screen.elements.empty_list

import com.gamapp.dmplayer.R
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable

@Composable
fun BoxScope.EmptyAlbumsImage() {
    EmptyListImage(icon = R.drawable.round_album_24, text = R.string.empty_albums)
}