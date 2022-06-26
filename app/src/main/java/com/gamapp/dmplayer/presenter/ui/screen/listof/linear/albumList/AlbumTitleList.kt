package com.gamapp.dmplayer.presenter.ui.screen.listof.linear.albumList

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gamapp.dmplayer.presenter.ui.screen.elements.empty_list.EmptyAlbumsImage
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.BaseVerticalTitledList
import com.gamapp.domain.models.AlbumModel


@Composable
fun AlbumTitleList(
    title: String,
    items: List<AlbumModel>,
    state: AlbumsState,
    popupList: List<Pair<String, (AlbumModel) -> Unit>>,
    onAlbumClick: (AlbumModel) -> Unit,
    popBack: () -> Unit
) {
    BaseVerticalTitledList(
        title = title,
        items = items,
        state = state,
        modifier = Modifier,
        onEmptyListImage = {
            EmptyAlbumsImage()
        },
        onItemClicked = onAlbumClick,
        other = {

        },
        popupList = popupList,
        popBack = popBack
    )
}