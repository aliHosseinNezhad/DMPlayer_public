package com.gamapp.domain.usecase.data.search

import com.gamapp.domain.models.AlbumModel
import com.gamapp.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlbumsBySearchUseCase @Inject constructor(private val repository:SearchRepository) {
    fun invoke(): Flow<List<AlbumModel>> {
        return repository.albums()
    }

}