package com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.dialogs
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList.basetracks.TrackSortBarVisibility
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList.basetracks.rememberBaseTrackState
import com.gamapp.dmplayer.presenter.ui.screen.menu.tracks.string
import com.gamapp.dmplayer.presenter.viewmodel.TracksViewModel
import com.gamapp.dmplayer.R

@Composable
fun TracksByFavorite(viewModel: TracksViewModel, nav: NavHostController) {
    val tracks by remember(viewModel) {
        viewModel.favoriteInteracts.getAllFavorites.invoke()
    }.observeAsState(emptyList())
    val dialog = dialogs()
    CategoryTracks(
        tracks = tracks,
        popups = viewModel.menu.trackByFavoriteMenu.menu(dialog, nav).string(),
        state = rememberBaseTrackState {
            sortBarVisibility = TrackSortBarVisibility.Visible(
                viewModel.trackInteracts.sortOrder.invoke()
            )
        },
        categoryTitle = stringResource(id = R.string.favorite),
        popBack = {
            nav.popBackStack()
        })
}