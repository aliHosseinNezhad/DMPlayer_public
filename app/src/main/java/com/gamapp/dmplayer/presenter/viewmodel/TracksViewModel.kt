package com.gamapp.dmplayer.presenter.viewmodel

import android.app.Application
import com.gamapp.dmplayer.presenter.ui.screen.menu.MenuProvider
import com.gamapp.dmplayer.presenter.ui.screen.menu.tracks.TracksMenu
import com.gamapp.domain.repository.*
import com.gamapp.domain.usecase.interacts.QueueInteracts
import com.gamapp.domain.usecase.interacts.FavoriteInteracts
import com.gamapp.domain.usecase.interacts.Interacts
import com.gamapp.domain.usecase.interacts.TrackInteracts
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TracksViewModel @Inject constructor(
    application: Application,
    private val favoriteRepository: FavoriteRepository,
    private val queueRepository: QueueRepository,
    private val trackRepository: TrackRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    val queueInteracts: QueueInteracts,
    val trackInteracts: TrackInteracts,
    val interacts: Interacts,
    val favoriteInteracts: FavoriteInteracts,
    val tracksMenu: TracksMenu,
    val menu:MenuProvider
) : BaseViewModel(application),
    FavoriteRepository by favoriteRepository,
    QueueRepository by queueRepository,
    TrackRepository by trackRepository,
    AlbumRepository by albumRepository,
    ArtistRepository by artistRepository