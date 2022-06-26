package com.gamapp.dmplayer.presenter.ui.screen.elements.empty_list

import com.gamapp.dmplayer.R
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable

@Composable
fun BoxScope.EmptyTracksImage() {
    EmptyListImage(icon = R.drawable.ic_track, text = R.string.empty_track)
}