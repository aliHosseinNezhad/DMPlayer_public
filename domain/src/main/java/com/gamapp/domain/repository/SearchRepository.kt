package com.gamapp.domain.repository

import androidx.lifecycle.LiveData
import com.gamapp.domain.models.AlbumModel
import com.gamapp.domain.models.ArtistModel
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.sealedclasses.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface SearchRepository {
    val albumSortOrder: MutableStateFlow<Sort<AlbumModel>>
    val artistSortOrder: MutableStateFlow<Sort<ArtistModel>>
    val tracksSortOrder: MutableStateFlow<Sort<TrackModel>>
    fun albums(): Flow<List<AlbumModel>>
    fun artists(): Flow<List<ArtistModel>>
    fun tracks(): Flow<List<TrackModel>>
    fun setTitle(title: String): Boolean
    fun getTitle(): LiveData<String>
}