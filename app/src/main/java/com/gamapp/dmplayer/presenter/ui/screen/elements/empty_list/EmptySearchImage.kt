package com.gamapp.dmplayer.presenter.ui.screen.elements.empty_list

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import com.gamapp.dmplayer.R

@Composable
fun BoxScope.EmptySearchImage() {
    EmptyListImage(icon = R.drawable.outline_search_24, text = R.string.search_dialog)
}