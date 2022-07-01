package com.gamapp.dmplayer.presenter.ui.screen.listof.linear.albumList

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.BaseList
import com.gamapp.domain.models.AlbumModel

@Composable
fun BaseAlbums(
    state: AlbumsState = rememberAlbumState {},
    modifier: Modifier,
    albums: List<AlbumModel>,
    navigator: (AlbumModel) -> Unit
) {
    BaseList(
        items = albums,
        modifier = modifier,
        other = {},
        onItemClicked = {
            navigator(it)
        },
        state = state,
    )
}