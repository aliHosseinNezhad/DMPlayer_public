package com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList.basetracks

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.dmplayer.presenter.ui.screen.elements.empty_list.EmptyTracksImage
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.BaseList
import com.gamapp.dmplayer.presenter.viewmodel.AppViewModel
import com.gamapp.dmplayer.presenter.viewmodel.musicplayer.PlayerViewModel
import com.gamapp.domain.models.TrackModel
import kotlinx.coroutines.launch


@Composable
fun BaseTracks(
    state: BaseTrackState,
    items: List<TrackModel>,
    onPlay: (TrackModel) -> Unit,
    popups: List<Pair<String, (TrackModel) -> Unit>>,
    modifier: Modifier,
) {
    val scope = rememberCoroutineScope()
    val viewModel = hiltViewModel<AppViewModel>()
    val playerViewModel = hiltViewModel<PlayerViewModel>()
    val currentTrackId = playerViewModel.currentTrack.collectAsState().value?.id
    BaseList(
        state = state,
        items = items,
        modifier = modifier,
        other = {
            with(state) { FloatingButton() }
            TrackSelectionButtons(state.selectionManager)
        },
        isFocused = {
            currentTrackId == it.id
        },
        popupList = popups,
        onItemClicked = { item ->
            scope.launch {
                viewModel.setAndPlay(items, item)
                onPlay(item)
            }
        },
        emptyContent = {
            EmptyTracksImage()
        }
    )
}