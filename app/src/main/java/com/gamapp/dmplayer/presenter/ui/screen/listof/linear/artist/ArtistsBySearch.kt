package com.gamapp.dmplayer.presenter.ui.screen.listof.artist

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gamapp.dmplayer.presenter.ui.navigation.toAlbum
import com.gamapp.dmplayer.presenter.ui.navigation.toArtist
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.albumList.AlbumSortBarVisibility
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.albumList.rememberAlbumState
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.artist.ArtistsTitleList
import com.gamapp.dmplayer.presenter.viewmodel.SearchViewModel
import com.gamapp.domain.models.safe


@Composable
fun ArtistsBySearch(text: String, nav: NavHostController) {
    val viewModel = hiltViewModel<SearchViewModel>()
    val artists by remember(viewModel) {
        viewModel.searchInteracts.artist.invoke()
    }.collectAsState(initial = emptyList())
    LaunchedEffect(key1 = text) {
        viewModel.searchInteracts.setText.invoke(text)
    }
    ArtistsTitleList(
        title = text,
        items = artists,
        state = rememberArtistState {
            sortBarVisibility =
                ArtistSortBarVisibility.Visible(viewModel.searchInteracts.artistOrder.invoke())
        },
        popupList = viewModel.artistMenu.menu(nav = nav),
        onArtistClick = {
            nav.toArtist(it.safe.id)
        },
        popBack = {
            nav.popBackStack()
        })
}