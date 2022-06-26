package com.gamapp.dmplayer.presenter.ui.screen.menu.tracks

import androidx.navigation.NavHostController
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.DialogsState
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.StringResource
import com.gamapp.domain.usecase.interacts.FavoriteInteracts
import kotlinx.coroutines.launch
import javax.inject.Inject

class TrackByFavoriteMenu @Inject constructor(
    private val interacts: FavoriteInteracts,
    private val tracksMenu: TracksMenu
) {
    fun menu(dialog: DialogsState,nav:NavHostController): List<TrackMenuTypeAlias> {
        return tracksMenu.menu(dialog,nav) + listOf(
            StringResource(R.string.dislike) to {
                dialog.scope?.launch {
                    interacts.remove.invoke(it)
                }
            }
        )
    }
}