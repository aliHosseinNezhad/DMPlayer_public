package com.gamapp.dmplayer.presenter.ui.screen.listof.linear.albumList

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gamapp.dmplayer.presenter.ui.navigation.toAlbum
import com.gamapp.dmplayer.presenter.viewmodel.SearchViewModel
import com.gamapp.domain.models.safe

@Composable
fun AlbumsBySearch(text: String, nav: NavHostController) {
    val viewModel: SearchViewModel = hiltViewModel()
    val albums by remember {
        viewModel.searchInteracts.albums.invoke()
    }.collectAsState(initial = emptyList())
    LaunchedEffect(key1 = text) {
        viewModel.searchInteracts.setText.invoke(text)
    }
    AlbumTitleList(
        title = text,
        items = albums,
        state = rememberAlbumState {
            sortBarVisibility =
                AlbumSortBarVisibility.Visible(viewModel.searchInteracts.albumOrder.invoke())
        },
        popupList = viewModel.albumMenu.menu(nav = nav),
        onAlbumClick = {
            nav.toAlbum(it.safe.id)
        },
        popBack = {
            nav.popBackStack()
        })
}