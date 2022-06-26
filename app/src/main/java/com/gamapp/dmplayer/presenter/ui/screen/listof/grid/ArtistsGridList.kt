package com.gamapp.dmplayer.presenter.ui.screen.listof.grid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.gamapp.dmplayer.presenter.ui.navigation.toArtist
import com.gamapp.dmplayer.presenter.ui.screen.elements.CategoryItem
import com.gamapp.dmplayer.presenter.ui.screen.elements.empty_list.EmptyArtistsImage
import com.gamapp.dmplayer.presenter.ui.screen.elements.sort_elements.ArtistSortLayout
import com.gamapp.dmplayer.presenter.viewmodel.CategoryViewModel


@Composable
fun ArtistsGridList(
    categoryViewModel: CategoryViewModel,
    nav: NavHostController,
) {
    val artists by remember(categoryViewModel) {
        categoryViewModel.artists()
    }.collectAsState(emptyList())
    BaseGridCategoryItems(TopItem = {
        ArtistSortLayout(
            selection = false,
            categoryViewModel.artistInteracts.sortOrder.invoke(),
            modifier = Modifier.zIndex(2f)
        )
    }, item = {
        CategoryItem(
            id = it.imageId,
            title = it.title,
            subtitle1 = "${it.albums.size} albums ",
            subtitle2 = "| ${it.count} tracks"
        ) {
            nav.toArtist(it.id)
        }
    }, items = artists, key = { it.id },
        emptyListImage = {
            EmptyArtistsImage()
        })
}
