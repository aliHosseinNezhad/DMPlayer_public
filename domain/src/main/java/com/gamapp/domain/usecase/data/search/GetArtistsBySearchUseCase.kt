package com.gamapp.domain.usecase.data.search

import com.gamapp.domain.models.ArtistModel
import com.gamapp.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArtistsBySearchUseCase @Inject constructor(private val repository:SearchRepository) {

    fun invoke(): Flow<List<ArtistModel>> {
        return repository.artists()
    }
}