package com.gamapp.dmplayer.presenter.ui.screen.menu

import com.gamapp.dmplayer.presenter.ui.screen.menu.tracks.TrackByFavoriteMenu
import com.gamapp.dmplayer.presenter.ui.screen.menu.tracks.TrackByQueueMenu
import com.gamapp.dmplayer.presenter.ui.screen.menu.tracks.TracksMenu
import javax.inject.Inject

class MenuProvider @Inject constructor(
    val tracksMenu: TracksMenu,
    val trackByQueue: TrackByQueueMenu,
    val trackByFavoriteMenu: TrackByFavoriteMenu,
    val nonDefaultQueueItemMenu: NonDefaultQueueItemMenu,
    val defaultQueueItemMenu: NonDefaultQueueItemMenu
)