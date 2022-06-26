package com.gamapp.domain.usecase.data.album

import com.gamapp.domain.models.AlbumModel
import com.gamapp.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlbumUseCase @Inject constructor(private val repository: AlbumRepository) {
    fun invoke(): Flow<List<AlbumModel>> {
        return repository.albums()
    }
}