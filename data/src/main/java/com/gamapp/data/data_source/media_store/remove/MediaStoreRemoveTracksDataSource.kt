package com.gamapp.data.data_source.media_store.remove

import androidx.activity.result.ActivityResultRegistryOwner
import com.gamapp.domain.models.BaseTrack
import com.gamapp.domain.models.TrackModel

interface MediaStoreRemoveTracksDataSource {
    suspend fun removeTrack(track:BaseTrack)
    suspend fun removeTracks(tracks:List<BaseTrack>)
}