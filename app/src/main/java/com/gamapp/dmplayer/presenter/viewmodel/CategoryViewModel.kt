package com.gamapp.dmplayer.presenter.viewmodel

import android.app.Application
import com.gamapp.domain.repository.*
import com.gamapp.domain.usecase.interacts.AlbumInteracts
import com.gamapp.domain.usecase.interacts.ArtistInteracts
import com.gamapp.domain.usecase.interacts.QueueInteracts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject


//interface CategoryLoader {
//    fun albums(): Job
//    fun artists(): Job
//}

@HiltViewModel
class CategoryViewModel @Inject constructor(
    application: Application,
    private val queueRepository: QueueRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    val albumInteracts: AlbumInteracts,
    val artistInteracts: ArtistInteracts,
    val queueInteracts: QueueInteracts
) : BaseViewModel(application), QueueRepository by queueRepository,
    AlbumRepository by albumRepository, ArtistRepository by artistRepository