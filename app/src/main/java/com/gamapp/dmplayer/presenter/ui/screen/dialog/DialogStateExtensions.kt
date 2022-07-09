package com.gamapp.dmplayer.presenter.ui.screen.dialog

import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.*
import com.gamapp.domain.models.BaseTrackModel
import com.gamapp.domain.models.TrackModel

fun DialogsState.showRemoveTrackDialog(
    tracks: List<TrackModel>,
    onRemove: suspend (tracks: List<TrackModel>) -> Unit
) {
    if (tracks.isEmpty()) return
    if (tracks.size == 1) {
        show(
            RemoveDialogData(
                texts = WarningDialogTexts.RemoveTrack,
                onAccept = {
                    onRemove(tracks)
                }
            )
        )
    } else {
        show(
            RemoveDialogData(
                texts = WarningDialogTexts.RemoveTracks(tracks.size),
                onAccept = {
                    onRemove(tracks)
                }
            )
        )
    }
}


fun DialogsState.showAddToQueueDialog(tracks: List<TrackModel>, finish: () -> Unit) {
    show(
        AddToQueueDialogData(
            tracks = tracks,
            finish = finish
        )
    )
}