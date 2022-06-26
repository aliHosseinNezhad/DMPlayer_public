package com.gamapp.domain.repository

import androidx.compose.runtime.State
import androidx.lifecycle.LiveData
import com.gamapp.domain.models.TrackModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface FavoriteRepository {
    suspend fun addToFavorite(trackModel: TrackModel)
    suspend fun removeFromFavorite(fileId: Long)
    suspend fun removeFromFavorite(tracks:List<TrackModel>)
    suspend fun clear()
    fun getAllFavorites(): Flow<List<TrackModel>>
    fun isFavorite(fileId: Long): LiveData<Boolean>
}