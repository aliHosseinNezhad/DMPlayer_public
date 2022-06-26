package com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.dialogs
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList.basetracks.TrackSortBarVisibility
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList.basetracks.rememberBaseTrackState
import com.gamapp.dmplayer.presenter.ui.screen.menu.tracks.string
import com.gamapp.dmplayer.presenter.viewmodel.SearchViewModel
import kotlinx.coroutines.Dispatchers


@Composable
fun TracksBySearch(title: String, nav: NavHostController) {
    val searchViewModel = hiltViewModel<SearchViewModel>()
    val tracks by remember {
        searchViewModel.searchInteracts.tracks.invoke()
    }.collectAsState(initial = emptyList(), context = Dispatchers.IO)
    LaunchedEffect(key1 = title) {
        searchViewModel.searchInteracts.setText.invoke(title)
    }
    val dialog = dialogs()
    CategoryTracks(
        tracks = tracks, popups = searchViewModel
            .tracksMenu
            .menu(dialog, nav).string(),
        state = rememberBaseTrackState {
            sortBarVisibility =
                TrackSortBarVisibility
                    .Visible(
                        searchViewModel.searchInteracts
                            .trackOrder.invoke()
                    )
        },
        categoryTitle = title,
        popBack = {
            nav.popBackStack()
        }
    )
}