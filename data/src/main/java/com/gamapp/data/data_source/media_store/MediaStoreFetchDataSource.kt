package com.gamapp.data.data_source.media_store

import com.gamapp.domain.models.AlbumModel
import com.gamapp.domain.models.ArtistModel
import com.gamapp.domain.models.TrackModel
import kotlinx.coroutines.flow.Flow


interface MediaStoreFetchDataSource {

    fun getAllTracks(): Flow<List<TrackModel>>

    fun getAllAlbums(): Flow<List<AlbumModel>>

    fun getAllArtists(): Flow<List<ArtistModel>>

    fun getAlbumById(albumId: Long): Flow<AlbumModel>

    fun getArtistById(artistId: Long): Flow<ArtistModel>

    /**
     * @param ids list of tracks ids
     * */
    fun getTracksByIds(ids: List<Long>): Flow<List<TrackModel>>
}