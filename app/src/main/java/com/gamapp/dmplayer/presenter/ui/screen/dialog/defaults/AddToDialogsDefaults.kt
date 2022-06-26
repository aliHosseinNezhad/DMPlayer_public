package com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.gamapp.dmplayer.presenter.ui.screen.dialog.add_musics_to_queues_dialog.AddMusicsToQueuesDialog
import com.gamapp.domain.models.TrackModel

data class AddToQueueDialogData(
    val tracks: List<TrackModel>,
    val finish: () -> Unit
) : DialogEvent.Visible()


@Composable
fun AddToQueueDialogDefault(dialog: DialogsState) {
    val visibility by dialog.event()
    val show by remember {
        derivedStateOf {
            visibility is AddToQueueDialogData
        }
    }
    AddMusicsToQueuesDialog(
        show = show,
        tracks = if (show) (visibility as AddToQueueDialogData).tracks else emptyList(),
        finish = {
            (visibility as AddToQueueDialogData).finish()
            dialog.finish()
        })
}