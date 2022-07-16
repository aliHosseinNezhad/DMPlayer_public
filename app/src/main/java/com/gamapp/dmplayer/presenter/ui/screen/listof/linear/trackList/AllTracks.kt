package com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.dialogs
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList.basetracks.BaseTracks
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList.basetracks.TrackSortBarVisibility
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList.basetracks.rememberBaseTrackState
import com.gamapp.dmplayer.presenter.ui.screen.menu.tracks.string
import com.gamapp.dmplayer.presenter.viewmodel.AppViewModel
import com.gamapp.dmplayer.presenter.viewmodel.TracksViewModel

@Composable
fun AllTracks(viewModel: TracksViewModel = viewModel(), nav: NavHostController) {
    val appViewModel = viewModel<AppViewModel>()
    val tracks by remember(viewModel) {
        viewModel.trackInteracts.get.invoke()
    }.observeAsState(emptyList())
    val state = rememberBaseTrackState {
        sortBarVisibility = TrackSortBarVisibility.Visible(
            viewModel.trackInteracts.sortOrder.invoke(),
        )
    }
    state.SelectionLaunchedScope(
        tracks = tracks,
        topBarType = appViewModel.topBarType
    )
    val dialog = dialogs()
    BaseTracks(
        state = state,
        items = tracks,
        onPlay = {},
        popups = viewModel.tracksMenu.menu(dialog,nav = nav).string(),
        modifier = Modifier.fillMaxSize()
    )
}