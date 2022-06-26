package com.gamapp.domain.usecase.data.album

import androidx.activity.result.ActivityResultRegistryOwner
import com.gamapp.domain.model_usecase.remove
import com.gamapp.domain.models.AlbumModel
import com.gamapp.domain.repository.AlbumRepository
import com.gamapp.domain.usecase.data.tracks.RemoveTrackUseCase
import com.gamapp.domain.usecase.interacts.Interacts
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoveAlbumUseCase @Inject constructor(private val removeTrackUseCase: RemoveTrackUseCase) {
    suspend fun invoke(albums: List<AlbumModel>) {
        val tracks = albums.flatMap { it.tracks }
        removeTrackUseCase.invoke(tracks)
    }
}