package com.gamapp.dmplayer.presenter.ui.screen.menu.tracks

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.navigation.toTrackDetails
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.*
import com.gamapp.dmplayer.presenter.ui.screen.dialog.showAddToQueueDialog
import com.gamapp.dmplayer.presenter.utils.openMusicWith
import com.gamapp.dmplayer.presenter.utils.shareMusic
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.usecase.interacts.TrackInteracts
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


typealias TrackMenuTypeAlias = (Pair<StringResource, (track: TrackModel) -> Unit>)
typealias TrackStringMenuTypeAlias = Pair<String, (track: TrackModel) -> Unit>

@Composable
fun List<TrackMenuTypeAlias>.string(): List<TrackStringMenuTypeAlias> {
    return map {
        it.first.string() to it.second
    }
}

fun List<TrackMenuTypeAlias>.string(context: Context): List<TrackStringMenuTypeAlias> {
    return map {
        it.first.string(context) to it.second
    }
}

class TracksMenu @Inject constructor(
    @ApplicationContext private val context: Context,
    private val trackInteracts: TrackInteracts,
) {
    fun menu(dialog: DialogsState, nav: NavHostController): List<TrackMenuTypeAlias> {
        return listOf(
            StringResource(R.string.menu_add_to) to {
                dialog.showAddToQueueDialog(listOf(it)) {}
            },
            StringResource(R.string.share) to {
                context.shareMusic(it.id)
            },
            StringResource(R.string.menu_open_with) to {
                context.openMusicWith(it.id)
            },
            StringResource(R.string.remove_track) to {
                dialog.show(
                    RemoveDialogData(
                        WarningDialogTexts.RemoveTrack,
                        onAccept = {
                            trackInteracts.remove.invoke(it)
                        },
                        onCancel = {

                        }
                    )
                )
            },
            StringResource(R.string.track_details) to {
                nav.toTrackDetails(it)
            }
        )
    }
}