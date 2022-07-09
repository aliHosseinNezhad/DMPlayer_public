package com.gamapp.domain.repository

import android.content.ContentValues
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.lifecycle.LiveData
import com.gamapp.domain.models.AlbumModel
import com.gamapp.domain.models.ArtistModel
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.sealedclasses.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface TrackRepository {
    fun tracks(): LiveData<List<TrackModel>>
    fun artist(id: Long): LiveData<ArtistModel>
    fun album(id: Long): LiveData<AlbumModel>
    fun trackByQueue(id: String): LiveData<List<TrackModel>>
    fun trackByFavorite(): LiveData<List<TrackModel>>
    suspend fun removeTrack(track: TrackModel)
    suspend fun removeTracks(tracks: List<TrackModel>)
    suspend fun updateTracks(tracks: List<TrackModel>, contentValues: ContentValues)
    fun getTracksById(ids: List<Long>): Flow<List<TrackModel>>
    val trackSortOrder: MutableStateFlow<Sort<TrackModel>>
}