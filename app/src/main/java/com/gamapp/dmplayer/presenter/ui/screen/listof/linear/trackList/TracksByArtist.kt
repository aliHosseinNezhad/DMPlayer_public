package com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavHostController
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.dialogs
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList.basetracks.TrackSortBarVisibility
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList.basetracks.rememberBaseTrackState
import com.gamapp.dmplayer.presenter.ui.screen.menu.tracks.string
import com.gamapp.dmplayer.presenter.viewmodel.TracksViewModel
import com.gamapp.domain.models.ArtistModel


@Composable
fun TracksByArtist(
    artistId: Long,
    viewModel: TracksViewModel,
    nav: NavHostController,
) {
    val artist by remember(viewModel) {
        viewModel.artist(artistId)
    }.observeAsState(ArtistModel.empty)
    val dialog = dialogs()
    CategoryTracks(
        tracks = artist.tracks,
        popups = viewModel.tracksMenu.menu(dialog,nav).string(),
        state = rememberBaseTrackState {
            sortBarVisibility = TrackSortBarVisibility.Visible(viewModel.trackInteracts.sortOrder.invoke())
        },
        categoryTitle = artist.title,
        popBack = {
            nav.popBackStack()
        }
    )
}