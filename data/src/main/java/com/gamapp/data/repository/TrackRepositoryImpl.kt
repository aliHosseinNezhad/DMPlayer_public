package com.gamapp.data.repository

import android.content.ContentValues
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.gamapp.data.data_source.media_store.MediaStoreFetchDataSource
import com.gamapp.data.data_source.media_store.MediaStoreUpdateTrackDataSource
import com.gamapp.data.data_source.media_store.remove.MediaStoreRemoveTracksDataSource
import com.gamapp.domain.models.AlbumModel
import com.gamapp.domain.models.ArtistModel
import com.gamapp.domain.models.BaseTrack
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.repository.FavoriteRepository
import com.gamapp.domain.repository.QueueRepository
import com.gamapp.domain.repository.TrackRepository
import com.gamapp.domain.sealedclasses.Order
import com.gamapp.domain.sealedclasses.Sort
import com.gamapp.domain.sealedclasses.TrackSort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class TrackRepositoryImpl @Inject constructor(
    private val mediaStoreFetchDataSource: MediaStoreFetchDataSource,
    private val queueRepository: QueueRepository,
    private val favoriteRepository: FavoriteRepository,
    private val removeTracksDataSource: MediaStoreRemoveTracksDataSource,
    private val updateTrackDataSource: MediaStoreUpdateTrackDataSource
) : TrackRepository {
    override val trackSortOrder: MutableStateFlow<Sort<TrackModel>> = MutableStateFlow(
        TrackSort(
            TrackSort.SortByDateAdded,
            Order.ETS
        )
    )
    override fun artist(id: Long): LiveData<ArtistModel> {
        return mediaStoreFetchDataSource.getArtistById(id)
            .combine(trackSortOrder, transform = { a, b ->
                val tracks = b.method(a.tracks)
                ArtistModel(a.id, tracks)
            }).asLiveData(Dispatchers.IO)
    }


    override fun tracks(): LiveData<List<TrackModel>> {
        return mediaStoreFetchDataSource.getAllTracks()
            .combine(trackSortOrder, transform = { a, b ->
                b.method(a)
            }).asLiveData(Dispatchers.IO)
    }

    override fun album(id: Long): LiveData<AlbumModel> {
        return mediaStoreFetchDataSource.getAlbumById(id)
            .combine(trackSortOrder, transform = { a, b ->
                val tracks = b.method(a.tracks)
                AlbumModel(a.id, tracks)
            }).asLiveData(Dispatchers.IO)
    }

    override fun trackByFavorite(): LiveData<List<TrackModel>> {
        return favoriteRepository.getAllFavorites().combine(trackSortOrder, transform = { a, b ->
            b.method(a)
        }).asLiveData(Dispatchers.IO)
    }

    override fun trackByQueue(id: String): LiveData<List<TrackModel>> {
        return queueRepository.getAllMusicByQueueIdViaFlow(id)
            .combine(trackSortOrder, transform = { a, b ->
                b.method(a)
            }).asLiveData(Dispatchers.IO)
    }


    override suspend fun removeTrack(
        track: BaseTrack,
    ) {
        removeTracksDataSource.removeTrack(track)
    }

    override suspend fun removeTracks(
        tracks: List<BaseTrack>,
    ) {
        removeTracksDataSource.removeTracks(tracks)
    }

    override suspend fun updateTracks(tracks: List<BaseTrack>, contentValues: ContentValues) {
        updateTrackDataSource.update(tracks.map { it.id }, contentValues)
    }

    override fun getTracksById(ids: List<Long>): Flow<List<TrackModel>> {
        return mediaStoreFetchDataSource.getTracksByIds(ids)
    }
}