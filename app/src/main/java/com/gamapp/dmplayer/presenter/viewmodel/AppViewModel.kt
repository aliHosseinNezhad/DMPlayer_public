package com.gamapp.dmplayer.presenter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamapp.data.db.ApplicationDatastore
import com.gamapp.dmplayer.presenter.TopBarType
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.player_interface.PlayerConnector
import com.gamapp.domain.player_interface.PlayerData
import com.gamapp.domain.repository.FavoriteRepository
import com.gamapp.domain.repository.QueueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject




@HiltViewModel
class AppViewModel @Inject constructor(
    private val queueRepository: QueueRepository,
    private val favoriteRepository: FavoriteRepository,
    private val player: PlayerConnector,
    private val playerData: PlayerData,
    private val applicationDatastore: ApplicationDatastore,
) : ViewModel(), PlayerConnector by player,
    QueueRepository by queueRepository,
    FavoriteRepository by favoriteRepository,
    PlayerData by playerData,
    ApplicationDatastore by applicationDatastore {
    val topBarType: MutableStateFlow<TopBarType> = MutableStateFlow(TopBarType.None)
    fun setAndPlay(
        playList: List<TrackModel>,
        currentTrack: TrackModel
    ) {
        viewModelScope.launch {
            player.setPlayListAndPlay(currentTrack, playList)
        }
    }
}

