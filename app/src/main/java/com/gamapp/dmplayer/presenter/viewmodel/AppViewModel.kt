package com.gamapp.dmplayer.presenter.viewmodel

import androidx.lifecycle.ViewModel
import com.gamapp.data.db.ApplicationDatastore
import com.gamapp.dmplayer.presenter.TopBarType
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.player_interface.PlayerEvents
import com.gamapp.domain.repository.FavoriteRepository
import com.gamapp.domain.repository.QueueRepository
import com.gamapp.domain.usecase.interacts.PlayerInteracts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


@HiltViewModel
class AppViewModel @Inject constructor(
    private val queueRepository: QueueRepository,
    private val favoriteRepository: FavoriteRepository,
    private val playerEvents: PlayerEvents,
    private val applicationDatastore: ApplicationDatastore,
    val playerInteracts: PlayerInteracts,
) : ViewModel(),
    QueueRepository by queueRepository,
    FavoriteRepository by favoriteRepository,
    PlayerEvents by playerEvents,
    ApplicationDatastore by applicationDatastore {
    val topBarType: MutableStateFlow<TopBarType> = MutableStateFlow(TopBarType.None)
}

