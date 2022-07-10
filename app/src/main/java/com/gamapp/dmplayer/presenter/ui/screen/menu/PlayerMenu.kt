package com.gamapp.dmplayer.presenter.ui.screen.menu

import android.content.Context
import androidx.compose.runtime.State
import androidx.navigation.NavHostController
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.navigation.toTrackDetails
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.DialogsState
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.StringResource
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.WarningDialogData
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.WarningDialogTexts
import com.gamapp.dmplayer.presenter.utils.shareMusic
import com.gamapp.domain.model_usecase.remove
import com.gamapp.domain.models.BaseTrack
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.usecase.data.tracks.GetTracksByIdUseCase
import com.gamapp.domain.usecase.interacts.Interacts
import com.gamapp.domain.usecase.interacts.TrackInteracts
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

typealias PlayerMenuTypeAlias = Pair<StringResource, () -> Unit>

class PlayerMenu @Inject constructor(
    @ApplicationContext private val context: Context,
    private val trackInteracts: TrackInteracts,
    private val getTracksByIdUseCase: GetTracksByIdUseCase,
    private val act: Interacts
) {
    fun menu(
        track: State<BaseTrack?>,
        dialogsState: DialogsState,
        nav: NavHostController
    ): List<PlayerMenuTypeAlias> {
        return listOf(
            StringResource(R.string.share) to {
                track.value?.let {
                    context.shareMusic(it.id)
                }
            },
            StringResource(R.string.remove_track) to {
                track.value?.let {
                    dialogsState.show(WarningDialogData(
                        WarningDialogTexts.RemoveTrack,
                        onAccept = {
                            track.value?.let {
                                act.track.remove.invoke(it)

                            }
                        }
                    ))
                }
            },
            StringResource(R.string.track_details) to {
                track.value?.let {
                    nav.toTrackDetails(it)
                }
            }
        )
    }
}