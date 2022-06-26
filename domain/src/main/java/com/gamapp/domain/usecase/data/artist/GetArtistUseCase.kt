package com.gamapp.domain.usecase.data.artist

import com.gamapp.domain.models.AlbumModel
import com.gamapp.domain.models.ArtistModel
import com.gamapp.domain.repository.AlbumRepository
import com.gamapp.domain.repository.ArtistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArtistUseCase @Inject constructor(private val repository: ArtistRepository) {
    fun invoke(): Flow<List<ArtistModel>> {
        return repository.artists()
    }
}