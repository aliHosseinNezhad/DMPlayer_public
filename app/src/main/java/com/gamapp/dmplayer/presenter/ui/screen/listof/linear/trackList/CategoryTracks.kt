package com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.dmplayer.presenter.ui.screen.elements.empty_list.EmptyTracksImage
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.BaseVerticalTitledList
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList.basetracks.BaseTrackState
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList.basetracks.TrackSelectionButtons
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList.basetracks.rememberBaseTrackState
import com.gamapp.dmplayer.presenter.viewmodel.AppViewModel
import com.gamapp.dmplayer.presenter.viewmodel.musicplayer.PlayerViewModel
import com.gamapp.domain.models.TrackModel
import kotlinx.coroutines.launch

const val TAG = "NestScrollTesting"

@Composable
fun CategoryTracks(
    categoryTitle: String = "",
    tracks: List<TrackModel>,
    popups: List<Pair<String, (TrackModel) -> Unit>>,
    state: BaseTrackState = rememberBaseTrackState {},
    popBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val viewModel = hiltViewModel<AppViewModel>()
    BaseVerticalTitledList(
        title = categoryTitle,
        items = tracks,
        state = state,
        modifier = Modifier,
        onEmptyListImage = {
            EmptyTracksImage()
        },
        onItemClicked = { item ->
            scope.launch {
                viewModel.playerInteracts.setPlayListAndPlay(tracks, item)
            }
        },
        other = {
            with(state) { FloatingButton() }
            TrackSelectionButtons(state.selectionManager)
        },
        popupList = popups,
        popBack = popBack
    )
}