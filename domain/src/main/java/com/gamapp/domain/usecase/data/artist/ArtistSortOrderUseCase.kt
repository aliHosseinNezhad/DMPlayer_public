package com.gamapp.domain.usecase.data.artist

import com.gamapp.domain.models.AlbumModel
import com.gamapp.domain.models.ArtistModel
import com.gamapp.domain.repository.AlbumRepository
import com.gamapp.domain.repository.ArtistRepository
import com.gamapp.domain.sealedclasses.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class ArtistSortOrderUseCase @Inject constructor(private val repository: ArtistRepository) {
    fun invoke(): MutableStateFlow<Sort<ArtistModel>> {
        return repository.artistSortOrder
    }
}