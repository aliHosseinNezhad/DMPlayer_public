package com.gamapp.dmplayer.presenter.ui.screen.elements.empty_list


import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import com.gamapp.dmplayer.R

@Composable
fun BoxScope.EmptyArtistsImage() {
    EmptyListImage(icon = R.drawable.ic_artist, text = R.string.empty_artist)
}