package com.gamapp.dmplayer.presenter.ui.screen.listof.grid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.gamapp.dmplayer.presenter.ui.navigation.toAlbum
import com.gamapp.dmplayer.presenter.ui.screen.elements.CategoryItem
import com.gamapp.dmplayer.presenter.ui.screen.elements.empty_list.EmptyAlbumsImage
import com.gamapp.dmplayer.presenter.ui.screen.elements.sort_elements.AlbumSortLayout
import com.gamapp.dmplayer.presenter.viewmodel.CategoryViewModel
import com.gamapp.domain.models.safe


@Composable
fun AlbumsGridList(
    albumsViewModel: CategoryViewModel,
    nav: NavHostController,
) {
    val albums by remember(albumsViewModel) {
        albumsViewModel.albumInteracts.getAll.invoke()
    }.collectAsState(emptyList())
    BaseGridCategoryItems(TopItem = {
        AlbumSortLayout(
            selection = false,
            modifier = Modifier.align(Alignment.TopCenter).zIndex(2f),
            albumsViewModel.albumInteracts.sortOrder.invoke()
        )
    }, item = {
        CategoryItem(
            id = it.imageId,
            title = it.title,
            subtitle1 = it.artist,
            subtitle2 = " |  ${it.count} track${if (it.count > 1) "s" else ""}",
        ) {
            nav.toAlbum(it.safe.id)
        }
    }, items = albums,
        key = {
            it.id
        }, emptyListImage = {
            EmptyAlbumsImage()
        })
}