package com.gamapp.dmplayer.presenter.ui.screen.menu.tracks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.*
import com.gamapp.domain.models.QueueModel
import com.gamapp.domain.usecase.interacts.QueueInteracts
import javax.inject.Inject

class TrackByQueueMenu @Inject constructor(
    private val queueInteracts: QueueInteracts,
    private val tracksMenu: TracksMenu
) {
    @Composable
    fun menu(
        dialog: DialogsState,
        queue: QueueModel,
        nav: NavHostController
    ): List<TrackMenuTypeAlias> {
        return remember(dialog, queue, nav) {
            tracksMenu.menu(dialog, nav) + listOf(
                StringResource(R.string.track_remove_from_queue) to {
                    dialog.show(
                        RemoveDialogData(
                            WarningDialogTexts.RemoveTrackFromQueue,
                            onAccept = {
                                queueInteracts.removeTrackFromQueue.invoke(queue, it)
                            },
                        )
                    )
                }
            )
        }
    }
}