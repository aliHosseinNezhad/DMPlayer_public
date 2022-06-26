package com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.dialogs
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList.basetracks.TrackSortBarVisibility
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList.basetracks.rememberBaseTrackState
import com.gamapp.dmplayer.presenter.ui.screen.menu.tracks.string
import com.gamapp.dmplayer.presenter.viewmodel.TracksViewModel
import com.gamapp.domain.models.QueueModel

@NonRestartableComposable
@Composable
fun <T> rememberAsync(
    vararg key: Any = emptyArray(),
    invoke:@DisallowComposableCalls suspend () -> T
): State<T?> {
    val state = remember {
        mutableStateOf(null as T?)
    }
    LaunchedEffect(key) {
        state.value = invoke()
    }
    return state
}

@Composable
fun TracksByQueue(
    queueId: String,
    nav: NavHostController,
) {
    val viewModel: TracksViewModel = hiltViewModel()
    var queueModel by remember {
        mutableStateOf(null as QueueModel?)
    }
    LaunchedEffect(key1 = queueId) {
        queueModel = viewModel.queueInteracts.getQueues.invoke(queueId)
    }
//    val queueModel by rememberAsync(queueId){
//        viewModel.queueInteracts.getQueues.invoke(queueId)
//    }
    val tracks by remember(queueId, viewModel) {
        viewModel.queueInteracts.getTracks.invoke(queueId)
    }.observeAsState(emptyList())
    val show by remember {
        derivedStateOf {
            queueModel != null
        }
    }
    if (show) {
        val dialog = dialogs()
        val popups = remember {
            viewModel.menu.trackByQueue
        }.menu(dialog, queueModel!!, nav)
        CategoryTracks(
            categoryTitle = queueModel!!.title,
            tracks = tracks,
            popups = popups.string(),
            state = rememberBaseTrackState {
                sortBarVisibility = TrackSortBarVisibility.Visible(
                    viewModel.trackInteracts.sortOrder.invoke(),
                )
            },
            popBack = {
                nav.popBackStack()
            }
        )
    }
}