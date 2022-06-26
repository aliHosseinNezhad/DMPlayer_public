package com.gamapp.domain.usecase.data.album

import com.gamapp.domain.models.AlbumModel
import com.gamapp.domain.repository.AlbumRepository
import com.gamapp.domain.sealedclasses.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class AlbumSortOrderUseCase @Inject constructor(private val repository: AlbumRepository) {
    fun invoke(): MutableStateFlow<Sort<AlbumModel>> {
        return repository.albumSortOrder
    }
}